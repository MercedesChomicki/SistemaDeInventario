package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.PurchaseProductRequestDTO;
import com.inventario.Inventario.entities.PurchaseProduct;
import com.inventario.Inventario.services.PurchaseProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchase_products")
@RequiredArgsConstructor
public class PurchaseProductController {

    private final PurchaseProductService purchaseProductService;

    @GetMapping()
    public List<PurchaseProduct> getAllPurchaseProducts(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return purchaseProductService.getAllPurchaseProductsSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseProduct> getPurchaseProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(purchaseProductService.getPurchaseProductById(id));
    }

    @PostMapping
    public ResponseEntity<PurchaseProduct> createPurchaseProduct(@RequestBody PurchaseProductRequestDTO purchaseProductRequestDTO) {
        PurchaseProduct newPurchaseProduct = purchaseProductService.createPurchaseProduct(purchaseProductRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPurchaseProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseProduct> updatePurchaseProduct(@PathVariable Integer id, @RequestBody PurchaseProductRequestDTO updatedPurchaseProduct) {
        PurchaseProduct updated = purchaseProductService.updatePurchaseProduct(id, updatedPurchaseProduct);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePurchaseProduct(@PathVariable Integer id) {
        purchaseProductService.deletePurchaseProduct(id);
        return ResponseEntity.ok("Se ha eliminado exitosamente.");
    }
}
