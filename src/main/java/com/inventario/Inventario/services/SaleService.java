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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    private static final BigDecimal CARD_SURCHARGE_PERCENTAGE = BigDecimal.valueOf(7); // 7% adicional por pago con tarjeta

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
        // Restricción: si la venta no se va a pagar en el momento, se debe guardar con el precio total en efectivo
        if ((dto.getPaymentMethod() == PaymentMethod.CARD || dto.getPaymentMethod() == PaymentMethod.MIXED)
                && dto.getStatus() == SaleStatus.PENDING) {
            throw new IllegalArgumentException("Método de pago y estado de la venta incompatibles.");
        }

        // 1. Crear la venta
        Sale sale = new Sale();
        sale.setTotal(BigDecimal.valueOf(0.01)); // setea el total con un valor minimo > 0
        sale.setPaymentMethod(dto.getPaymentMethod());
        sale.setStatus(dto.getStatus().equals(SaleStatus.PENDING) ? SaleStatus.PENDING : SaleStatus.PAID);
        sale.setDate(dto.getDate());

        // Guardamos la venta, sin detalles aún
        sale = saleRepository.save(sale);

        BigDecimal total = BigDecimal.ZERO;
        List<SaleDetail> details = new ArrayList<>();

        // 2. Agregar los detalles de la venta
        for (SaleDetailRequestDTO detailDTO : dto.getDetails()) {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", detailDTO.getProductId()));

            SaleDetail detail = new SaleDetail();
            detail.setSale(sale);
            detail.setProduct(product);
            detail.setQuantity(detailDTO.getQuantity());
            detail.setUnitPrice(product.getCashPrice());
            detail.setSubtotal(product.getCashPrice().multiply(BigDecimal.valueOf(detailDTO.getQuantity())));

            details.add(detail);
            total = total.add(detail.getSubtotal());

            // Descontar del stock los productos vendidos
            int deductFromStock = detailDTO.getQuantity();
            int stock = product.getStock()-deductFromStock;
            if(stock < 0) {
                throw new IllegalArgumentException("El stock del producto '"+product.getName()+"' es insuficiente");
            }
            product.setStock(stock);
        }

        // 3. Si la lista está vacía, mostrar error
        if (details.isEmpty()) {
            throw new IllegalArgumentException("No se puede crear una venta sin productos");
        }

        // 4. Guardar todos los detalles en 'sale_details' en una sola operación
        saleDetailRepository.saveAll(details);

        if (dto.getPaymentMethod() == PaymentMethod.CARD) {
            BigDecimal surcharge = total.multiply(CARD_SURCHARGE_PERCENTAGE).divide(BigDecimal.valueOf(100));
            total = total.add(surcharge);
            sale.setPayInCash(BigDecimal.ZERO);
            sale.setPayByCard(total);
        } else if (dto.getPaymentMethod() == PaymentMethod.MIXED) {
            if(dto.getPaidInCash() == null || dto.getPaidInCash().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Si elije el método de pago mixto, deberá ingresa el monto a pagar en efectivo");
            }
            if (dto.getPaidInCash().compareTo(total) >= 0) {
                throw new IllegalArgumentException("El pago en efectivo es mayor o igual al total.");
            }
            sale.setPayInCash(dto.getPaidInCash());
            BigDecimal rest = total.subtract(sale.getPayInCash());
            BigDecimal surcharge = rest.multiply(CARD_SURCHARGE_PERCENTAGE).divide(BigDecimal.valueOf(100));
            BigDecimal paidByCard = rest.add(surcharge);
            sale.setPayByCard(paidByCard);
            total = sale.getPayInCash().add(sale.getPayByCard());
        } else {
            if (dto.getPaidInCash().compareTo(total) > 0) {
                throw new IllegalArgumentException("El pago en efectivo es mayor que el total.");
            }
            sale.setPayInCash(total);
            sale.setPayByCard(BigDecimal.ZERO);
        }

        // 5. Actualizar el total y los detalles de la venta
        sale.setTotal(total);
        sale.setDetails(details);

        // 6. Si la venta es "pagar después", registrar la deuda
        if (dto.getStatus() == SaleStatus.PENDING) {
            Customer customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", dto.getCustomerId()));

            // Buscar si el cliente ya tiene una deuda activa (por ejemplo, que aún no ha sido pagada completamente)
            Optional<Debt> existingDebtOpt = debtRepository.findByCustomer(customer);

            BigDecimal payInCash = dto.getPaidInCash() != null ? dto.getPaidInCash() : BigDecimal.ZERO;

            Debt debt;
            if (existingDebtOpt.isPresent()) {
                debt = existingDebtOpt.get();
                debt.setAmountTotal(debt.getAmountTotal().add(total));
                debt.setAmountPaid(debt.getAmountPaid().add(payInCash));
                debt.setAmountDue(debt.getAmountDue().add(total.subtract(payInCash)));
                debt.setUpdatedAt(dto.getDate());
            } else {
                debt = new Debt();
                debt.setCustomer(customer);
                debt.setAmountTotal(sale.getTotal());
                debt.setAmountPaid(dto.getPaidInCash() != null ? dto.getPaidInCash() : BigDecimal.ZERO);
                debt.setAmountDue(sale.getTotal().subtract(debt.getAmountPaid()));
                debt.setCreatedAt(dto.getDate());
            }

            // Agregar la venta actual a la lista de ventas de la deuda
            debt.getSales().add(sale);
            sale.setDebt(debt);
            sale.setPayInCash(payInCash);
            sale.setPayByCard(BigDecimal.ZERO);

            debtRepository.save(debt);
        }

        // 7. Crear y retornar SaleResponseDTO
        return convertToDTO(sale);
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
