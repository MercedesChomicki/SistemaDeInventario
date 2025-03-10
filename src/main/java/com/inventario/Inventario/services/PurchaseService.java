package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.PurchaseRequestDTO;
import com.inventario.Inventario.dtos.PurchaseResponseDTO;
import com.inventario.Inventario.entities.*;
import com.inventario.Inventario.entities.Supplier;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.mappers.PurchaseMapper;
import com.inventario.Inventario.repositories.PurchaseRepository;
import com.inventario.Inventario.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final SupplierRepository supplierRepository;
    private final PurchasePaymentService paymentService;
    private final PurchaseMapper purchaseMapper;
    private final PurchaseManagerService purchaseManagerService;

    public List<PurchaseResponseDTO> getAllPurchasesSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return purchaseRepository.findAll(sort)
                .stream()
                .map(purchaseMapper::toDTO)
                .toList();
    }

    public PurchaseResponseDTO getPurchaseById(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra", id));
        return purchaseMapper.toDTO(purchase);
    }

    @Transactional
    public PurchaseResponseDTO createPurchase(PurchaseRequestDTO dto) {
        if (dto.getDetails().isEmpty())
            throw new IllegalArgumentException("Debe haber al menos un producto en la compra.");

        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", dto.getSupplierId()));

        Purchase purchase = new Purchase(supplier, dto.getSurcharge());

        List<PurchaseDetail> details = purchaseManagerService.processDetails(dto, purchase);
        BigDecimal total = paymentService.calculateTotalWithoutSurcharge(details).add(dto.getSurcharge());
        List<PurchasePayment> payments = paymentService.processPurchasePayments(purchase, dto.getPayments(), total.subtract(dto.getSurcharge()));

        purchase.setDetails(details);
        purchase.setTotal(total);
        purchase.setPayments(payments);
        purchaseRepository.save(purchase);
        return purchaseMapper.toDTO(purchase);
    }

    public void deletePurchase(Long id) {
        if (!purchaseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Compra", id);
        }
        purchaseRepository.deleteById(id);
    }
}
