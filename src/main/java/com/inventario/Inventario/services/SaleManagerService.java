package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.CartResponseDTO;
import com.inventario.Inventario.dtos.DetailResponseDTO;
import com.inventario.Inventario.dtos.SaleRequestDTO;
import com.inventario.Inventario.dtos.SaleResponseDTO;
import com.inventario.Inventario.entities.*;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.mappers.SaleMapper;
import com.inventario.Inventario.repositories.ProductRepository;
import com.inventario.Inventario.repositories.SaleDetailRepository;
import com.inventario.Inventario.repositories.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleManagerService {

    private final SaleRepository saleRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final SaleMapper saleMapper;
    private final ProductRepository productRepository;

    public Sale getSaleById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", id));
    }

    public Sale createAndSaveSale(){
        Sale sale = new Sale();
        saleRepository.save(sale);
        return sale;
    }

    public void validateSaleCreation(SaleRequestDTO dto, CartResponseDTO cart) {
        if (cart.getDetails() == null || cart.getDetails().isEmpty()) {
            throw new IllegalArgumentException("Debe haber al menos un producto registrado");
        }
        if (dto.getPayments() == null || dto.getPayments().isEmpty())
            throw new IllegalArgumentException("Debe haber al menos un pago registrado");
    }

    public List<SaleDetail> processAndSaveSaleDetails(Sale sale, List<DetailResponseDTO> cartDetails) {
        List<SaleDetail> details = cartDetails.stream()
                .map(detail -> {
                    Optional<Product> product = productRepository.findById(detail.getProductId());
                    return new SaleDetail(sale, product.orElse(null), detail.getQuantity());
                })
                .collect(Collectors.toList());
        sale.setDetails(details);
        saleDetailRepository.saveAll(details);
        return details;
    }

    public SaleResponseDTO saveSaleAndConvertToDTO(Sale sale, BigDecimal total, List<SalePayment> payments, List<SaleDetail> details) {
        updateSaleWithDetails(sale, total, payments, details);
        SaleResponseDTO dto = saleMapper.toDTO(sale);
        dto.setDetails(sale.getDetails().stream()
                .map(saleMapper::toDetailDTO) // Usa el mapper para cada detalle
                .toList());

        return dto;
    }

    private void updateSaleWithDetails(Sale sale, BigDecimal total, List<SalePayment> payments, List<SaleDetail> details) {
        sale.setTotal(total);
        sale.setPayments(payments);
        sale.setDate(LocalDateTime.now());
        sale.setDetails(details);
        saleRepository.save(sale);
    }
}
