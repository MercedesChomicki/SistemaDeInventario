package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.PurchaseRequestDTO;
import com.inventario.Inventario.entities.Purchase;
import com.inventario.Inventario.entities.Supplier;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.PurchaseRepository;
import com.inventario.Inventario.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final SupplierRepository supplierRepository;

    public List<Purchase> getAllPurchasesSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return purchaseRepository.findAll(sort);
    }

    public Purchase getPurchaseById(Integer id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra", id));
    }

    public Purchase createPurchase(PurchaseRequestDTO dto) {
        Map<String, Object> entities = fetchRelatedEntities(dto);

        Purchase purchase = new Purchase();
        purchase.setSupplier((Supplier) entities.get("supplier"));

        return purchaseRepository.save(purchase);
    }

    public Purchase updatePurchase(Integer id, PurchaseRequestDTO updatedPurchase) {
        Purchase existingPurchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase", id));

        Map<String, Object> entities = fetchRelatedEntities(updatedPurchase);

        if (updatedPurchase.getSupplierId() != null) existingPurchase.setSupplier((Supplier) entities.get("supplier"));

        return purchaseRepository.save(existingPurchase);
    }

    private Map<String, Object> fetchRelatedEntities(PurchaseRequestDTO dto) {
        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", dto.getSupplierId()));

        Map<String, Object> entities = new HashMap<>();
        entities.put("supplier", supplier);
        return entities;
    }

    public void deletePurchase(Integer id) {
        if (!purchaseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Compra", id);
        }
        purchaseRepository.deleteById(id);
    }
}
