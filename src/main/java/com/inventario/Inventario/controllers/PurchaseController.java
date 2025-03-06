package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.PurchaseRequestDTO;
import com.inventario.Inventario.dtos.PurchaseResponseDTO;
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
    public List<PurchaseResponseDTO> getAllPurchases(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return purchaseService.getAllPurchasesSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponseDTO> getPurchaseById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.getPurchaseById(id));
    }

    @PostMapping
    public ResponseEntity<PurchaseResponseDTO> createPurchase(@RequestBody PurchaseRequestDTO purchaseRequestDTO) {
        PurchaseResponseDTO newPurchase = purchaseService.createPurchase(purchaseRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPurchase);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePurchase(@PathVariable Long id) {
        purchaseService.deletePurchase(id);
        return ResponseEntity.ok("Se ha eliminado exitosamente.");
    }
}
