package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.*;
import com.inventario.Inventario.entities.*;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    public static final BigDecimal CARD_SURCHARGE_PERCENTAGE = BigDecimal.valueOf(7); // 7% adicional por pago con tarjeta

    private final SaleRepository saleRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final ProductRepository productRepository;
    private final DebtRepository debtRepository;
    private final CustomerRepository customerRepository;

    public List<SaleResponseDTO> getAllSalesSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        List<Sale> sales = saleRepository.findAll(sort);
        ArrayList<SaleResponseDTO> listDTO = new ArrayList<>();
        for(Sale sale : sales){
            SaleResponseDTO dto= convertToDTO(sale);
            listDTO.add(dto);
        }
        return listDTO;
    }

    public Sale getSaleById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito", id));
    }

    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO dto) {
        validateSale(dto);

        Sale sale = createSaleEntity(dto);
        List<SaleDetail> details = processSaleDetails(dto, sale);
        saleDetailRepository.saveAll(details);

        BigDecimal total = calculateTotal(details, dto.getPaymentMethod(), dto.getPaidInCash(), sale);

        if (dto.getStatus() == SaleStatus.PENDING) {
            registerDebt(dto, sale, total);
        }

        sale.setTotal(total);
        sale.setDetails(details);
        saleRepository.save(sale);

        return convertToDTO(sale);
    }

    private void validateSale(SaleRequestDTO dto) {
        // Restricción: si la venta no se va a pagar en el momento, se debe guardar con el precio total en efectivo
        if ((dto.getPaymentMethod() == PaymentMethod.CARD || dto.getPaymentMethod() == PaymentMethod.MIXED)
                && dto.getStatus() == SaleStatus.PENDING) {
            throw new IllegalArgumentException("Método de pago y estado de la venta incompatibles.");
        }
    }

    private Sale createSaleEntity(SaleRequestDTO dto) {
        Sale sale = new Sale();
        sale.setTotal(BigDecimal.valueOf(0.01)); // Valor mínimo
        sale.setPaymentMethod(dto.getPaymentMethod());
        sale.setStatus(dto.getStatus().equals(SaleStatus.PENDING) ? SaleStatus.PENDING : SaleStatus.PAID);
        sale.setDate(dto.getDate());
        return saleRepository.save(sale);
    }

    private List<SaleDetail> processSaleDetails(SaleRequestDTO dto, Sale sale) {
        List<SaleDetail> details = new ArrayList<>();

        for (SaleDetailRequestDTO detailDTO : dto.getDetails()) {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", detailDTO.getProductId()));

            if (product.getStock() < detailDTO.getQuantity()) {
                throw new IllegalArgumentException("Stock insuficiente para " + product.getName());
            }

            product.setStock(product.getStock() - detailDTO.getQuantity());

            SaleDetail detail = new SaleDetail();
            detail.setSale(sale);
            detail.setProduct(product);
            detail.setQuantity(detailDTO.getQuantity());
            detail.setUnitPrice(product.getCashPrice());
            detail.setSubtotal(product.getCashPrice().multiply(BigDecimal.valueOf(detailDTO.getQuantity())));

            details.add(detail);
        }

        if (details.isEmpty()) {
            throw new IllegalArgumentException("No se puede crear una venta sin productos");
        }

        return details;
    }

    private BigDecimal calculateTotal(List<SaleDetail> details, PaymentMethod paymentMethod, BigDecimal paidInCash, Sale sale) {
        BigDecimal total = details.stream().map(SaleDetail::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        switch (paymentMethod) {
            case CARD -> {
                BigDecimal surcharge = total
                        .multiply(CARD_SURCHARGE_PERCENTAGE)
                        .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
                sale.setPayInCash(BigDecimal.ZERO);
                sale.setPayByCard(total.add(surcharge));
                total = sale.getPayByCard();
            }
            case MIXED -> {
                if (paidInCash == null || paidInCash.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Debe ingresar el monto a pagar en efectivo.");
                }
                if (paidInCash.compareTo(total) >= 0) {
                    throw new IllegalArgumentException("El pago en efectivo no puede ser mayor o igual al total.");
                }
                BigDecimal rest = total.subtract(paidInCash);
                BigDecimal surcharge = rest.
                        multiply(CARD_SURCHARGE_PERCENTAGE)
                        .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
                sale.setPayInCash(paidInCash);
                sale.setPayByCard(rest.add(surcharge));
                total = sale.getPayInCash().add(sale.getPayByCard());
            }
            default -> {
                if (paidInCash.compareTo(total) > 0) {
                    throw new IllegalArgumentException("El pago en efectivo no puede ser mayor al total.");
                }
                sale.setPayInCash(total);
                sale.setPayByCard(BigDecimal.ZERO);
            }
        }

        return total;
    }

    private void registerDebt(SaleRequestDTO dto, Sale sale, BigDecimal total) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", dto.getCustomerId()));

        // Buscar si el cliente ya tiene una deuda activa
        Optional<Debt> existingDebtOpt = debtRepository.findByCustomer(customer);

        BigDecimal payInCash = dto.getPaidInCash() != null ? dto.getPaidInCash() : BigDecimal.ZERO;

        Debt debt = existingDebtOpt.orElseGet(() -> {
            Debt newDebt = new Debt();
            newDebt.setCustomer(customer);
            newDebt.setCreatedAt(dto.getDate());
            return newDebt;
        });

        debt.setAmountTotal(debt.getAmountTotal().add(total));
        debt.setAmountDue(debt.getAmountDue().add(total.subtract(payInCash)));
        debt.setAmountPaid(debt.getAmountPaid().add(payInCash));
        debt.setUpdatedAt(dto.getDate());

        debt.getSales().add(sale);
        sale.setDebt(debt);
        sale.setPayInCash(payInCash);
        sale.setPayByCard(BigDecimal.ZERO);

        debtRepository.save(debt);
    }

    private SaleResponseDTO convertToDTO(Sale sale) {
        SaleResponseDTO dto = new SaleResponseDTO();
        dto.setId(sale.getId());
        dto.setTotal(sale.getTotal());
        dto.setPaymentMethod(sale.getPaymentMethod());
        dto.setStatus(sale.getStatus());
        dto.setDate(sale.getDate());
        dto.setDebtId(sale.getDebt() != null ? sale.getDebt().getId() : null);
        dto.setPaidInCash(sale.getPayInCash());
        dto.setPaidByCard(sale.getPayByCard());

        List<SaleDetailResponseDTO> saleDetailDTOs = sale.getDetails().stream().map(detail -> {
            SaleDetailResponseDTO detailDTO = new SaleDetailResponseDTO();
            detailDTO.setProductId(detail.getProduct().getId());
            detailDTO.setQuantity(detail.getQuantity());
            detailDTO.setUnitPrice(detail.getUnitPrice());
            detailDTO.setSubtotal(detail.getSubtotal());
            return detailDTO;
        }).collect(Collectors.toList());

        dto.setDetails(saleDetailDTOs);
        return dto;
    }

   /* public List<SaleProductResponseDTO> getSaleProducts(Integer saleId){
        List<SaleProduct> saleProducts = saleProductRepository.findBySaleId(saleId);

        return saleProducts.stream()
                .map(cp -> new SaleProductResponseDTO(
                        cp.getProduct().getId(),
                        cp.getProduct().getName(),
                        cp.getQuantity(),
                        cp.getPrice()))
                .collect(Collectors.toList());
    }

    public BigDecimal calculateTotalPrice(List<SaleProductResponseDTO> saleProducts, boolean isCardPayment) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (SaleProductResponseDTO saleProduct : saleProducts) {
            Integer productId = saleProduct.getProductId();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));
            BigDecimal productPrice = isCardPayment ? product.getPriceWithCard(CARD_SURCHARGE_PERCENTAGE) : product.getPrice();
            totalPrice = totalPrice.add(
                    productPrice.multiply(BigDecimal.valueOf(saleProduct.getQuantity())))
                                .setScale(2, RoundingMode.HALF_UP); // Esto asegura que el resultado tenga solo dos decimales.
        }

        return totalPrice;
    }*/
}
