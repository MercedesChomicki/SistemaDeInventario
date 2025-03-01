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
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;
    private final SaleManagerService saleManagerService;
    private final StockService stockService;
    private final SalePaymentService paymentService;

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
        saleManagerService.validateSaleCreation(dto);
        Map<Integer, Product> productsMap = stockService.validateAndUpdateStock(dto.getDetails());
        Sale sale = saleManagerService.createAndSaveSale();
        List<SaleDetail> details = saleManagerService.processAndSaveSaleDetails(dto, sale, productsMap);
        BigDecimal total = paymentService.calculateTotal(details, dto.getPayments());
        List<SalePayment> payments = paymentService.processSalePayments(sale, dto.getPayments(), total);
        return saleManagerService.saveSaleAndConvertToDTO(sale, total, payments, details);
    }

    public void deleteSale(Long id) {
        if (!saleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Deuda", id);
        }
        saleRepository.deleteById(id);
    }
}
