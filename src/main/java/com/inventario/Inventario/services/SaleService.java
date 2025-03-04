package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.*;
import com.inventario.Inventario.entities.*;
import com.inventario.Inventario.mappers.SaleMapper;
import com.inventario.Inventario.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;
    private final SaleManagerService saleManagerService;
    private final SalePaymentService paymentService;
    private final CartService cartService;

    public List<SaleResponseDTO> getAllSalesSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return saleRepository.findAll(sort)
                .stream()
                .map(sale -> {
                    SaleResponseDTO dto = saleMapper.toDTO(sale);
                    // Mapear los detalles con el nombre del producto
                    dto.setDetails(sale.getDetails().stream()
                            .map(saleMapper::toDetailDTO)
                            .toList());
                    return dto;
                })
                .toList();
    }

    public SaleResponseDTO getSaleById(Long id) {
        Sale sale = saleManagerService.getSaleById(id);
        return saleMapper.toDTO(sale);
    }

    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO dto) {
        CartResponseDTO cart = cartService.getCartByUserId(dto.getUserId());
        saleManagerService.validateSaleCreation(dto, cart);
        Sale sale = saleManagerService.createAndSaveSale();
        List<SaleDetail> details = saleManagerService.processAndSaveSaleDetails(sale, cart.getDetails());
        BigDecimal total = paymentService.calculateTotal(details, dto.getPayments());
        List<SalePayment> payments = paymentService.processSalePayments(sale, dto.getPayments(), total);
        SaleResponseDTO saleDTO = saleManagerService.saveSaleAndConvertToDTO(sale, total, payments, details);
        cartService.clearCart(dto.getUserId());
        return saleDTO;
    }
}
