package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.*;
import com.inventario.Inventario.entities.*;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.mappers.SaleMapper;
import com.inventario.Inventario.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    public static final BigDecimal SURCHARGE_PERCENTAGE = BigDecimal.valueOf(7); // 7% adicional por pago con tarjeta

    private final SaleRepository saleRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final ProductRepository productRepository;
    private final SaleMapper saleMapper;
    private final ProductService productService;

    public List<SaleResponseDTO> getAllSalesSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return saleRepository.findAll(sort)
                .stream()
                .map(saleMapper::toDTO)
                .toList();
    }

    public Sale getSaleById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito", id));
    }

    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO dto) {
        Sale sale = new Sale();
        List<SaleDetail> details = processSaleDetails(dto, sale);
        saleDetailRepository.saveAll(details);

        List<SalePayment> payments = processSalePayments(dto.getPayments(), sale);
        BigDecimal total = payments.stream()
                .map(SalePayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        sale.setTotal(total);
        sale.setDate(LocalDateTime.now());
        sale.setDetails(details);
        sale.setPayments(payments);

        saleRepository.save(sale);
        return convertToDTO(sale);
    }

    private List<SaleDetail> processSaleDetails(SaleRequestDTO dto, Sale sale) {
        if (dto.getDetails() == null || dto.getDetails().isEmpty()) {
            throw new IllegalArgumentException("No se puede crear una venta sin productos");
        }
        return dto.getDetails().stream().map(detailDTO -> {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", detailDTO.getProductId()));

            productService.decreaseStock(product, detailDTO.getQuantity());

            return new SaleDetail(sale, product, detailDTO.getQuantity(),
                    product.getCashPrice(), product.getCashPrice().multiply(BigDecimal.valueOf(detailDTO.getQuantity())));
        }).toList();
    }

    private List<SalePayment> processSalePayments(List<SalePaymentRequestDTO> paymentDTOs, Sale sale) {
        List<SalePayment> payments = new ArrayList<>();

        for (SalePaymentRequestDTO paymentDTO : paymentDTOs) {
            BigDecimal amount = paymentDTO.getAmount();

            if (paymentDTO.getPaymentMethod() == PaymentMethod.CARD || paymentDTO.getPaymentMethod() == PaymentMethod.TRANSFER) {
                BigDecimal surcharge = calculateSurcharge(amount);
                amount = amount.add(surcharge);
            }

            SalePayment payment = new SalePayment();
            payment.setSale(sale);
            payment.setPaymentMethod(paymentDTO.getPaymentMethod());
            payment.setAmount(amount);

            payments.add(payment);
        }

        return payments;
    }

    private BigDecimal calculateSurcharge(BigDecimal total) {
        return total
                .multiply(SURCHARGE_PERCENTAGE)
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    }

    private SaleResponseDTO convertToDTO(Sale sale) {
        SaleResponseDTO dto = new SaleResponseDTO();
        dto.setId(sale.getId());
        dto.setTotal(sale.getTotal());
        dto.setDate(sale.getDate());

        List<SaleDetailResponseDTO> saleDetailDTOs = sale.getDetails().stream().map(detail -> {
            SaleDetailResponseDTO detailDTO = new SaleDetailResponseDTO();
            detailDTO.setProductId(detail.getProduct().getId());
            detailDTO.setQuantity(detail.getQuantity());
            detailDTO.setUnitPrice(detail.getUnitPrice());
            detailDTO.setSubtotal(detail.getSubtotal());
            return detailDTO;
        }).collect(Collectors.toList());

        List<SalePaymentResponseDTO> salePaymentDTOs = sale.getPayments().stream().map(payment -> {
            SalePaymentResponseDTO paymentDTO = new SalePaymentResponseDTO();
            paymentDTO.setPaymentMethod(payment.getPaymentMethod());
            paymentDTO.setAmount(payment.getAmount());
            return paymentDTO;
        }).toList();

        dto.setDetails(saleDetailDTOs);
        dto.setPayments(salePaymentDTOs);

        return dto;
    }
}
