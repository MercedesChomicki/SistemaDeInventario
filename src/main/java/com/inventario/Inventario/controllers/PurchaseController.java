package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.PurchaseRequestDTO;
import com.inventario.Inventario.entities.Purchase;
import com.inventario.Inventario.services.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @GetMapping()
    public List<Purchase> getAllPurchases(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return purchaseService.getAllPurchasesSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Purchase> getPurchaseById(@PathVariable Integer id) {
        return ResponseEntity.ok(purchaseService.getPurchaseById(id));
    }

    @PostMapping
    public ResponseEntity<Purchase> createPurchase(@RequestBody PurchaseRequestDTO purchaseRequestDTO) {
        Purchase newPurchase = purchaseService.createPurchase(purchaseRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPurchase);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Purchase> updatePurchase(@PathVariable Integer id, @RequestBody PurchaseRequestDTO updatedPurchase) {
        Purchase updated = purchaseService.updatePurchase(id, updatedPurchase);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePurchase(@PathVariable Integer id) {
        purchaseService.deletePurchase(id);
        return ResponseEntity.ok("Se ha eliminado exitosamente.");
    }
}
