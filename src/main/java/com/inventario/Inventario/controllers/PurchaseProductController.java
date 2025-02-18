package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.PurchaseProductRequestDTO;
import com.inventario.Inventario.dtos.PurchaseProductResponseDTO;
import com.inventario.Inventario.entities.PurchaseProduct;
import com.inventario.Inventario.entities.PurchaseProductId;
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
    public List<PurchaseProductResponseDTO> getAllPurchaseProducts(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return purchaseProductService.getAllPurchaseProductsSorted(sortBy, direction);
    }

    @GetMapping("/{purchaseId}/{productId}")
    public ResponseEntity<PurchaseProductResponseDTO> getPurchaseProductById(
            @PathVariable Integer purchaseId,
            @PathVariable Integer productId
    ) {
        PurchaseProductId id = new PurchaseProductId(purchaseId, productId);
        return ResponseEntity.ok(purchaseProductService.getPurchaseProductById(id));
    }

    @PostMapping
    public ResponseEntity<PurchaseProduct> createPurchaseProduct(@RequestBody PurchaseProductRequestDTO purchaseProductRequestDTO) {
        PurchaseProduct newPurchaseProduct = purchaseProductService.createPurchaseProduct(purchaseProductRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPurchaseProduct);
    }

    @PutMapping("/{purchaseId}/{productId}")
    public ResponseEntity<PurchaseProductResponseDTO> updatePurchaseProduct(
            @PathVariable Integer purchaseId,
            @PathVariable Integer productId,
            @RequestBody PurchaseProductRequestDTO updatedPurchaseProduct
    ) {
        PurchaseProductId id = new PurchaseProductId(purchaseId, productId);
        PurchaseProductResponseDTO updated = purchaseProductService.updatePurchaseProduct(id, updatedPurchaseProduct);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{purchaseId}/{productId}")
    public ResponseEntity<String> deletePurchaseProduct(
            @PathVariable Integer purchaseId,
            @PathVariable Integer productId
    ) {
        PurchaseProductId id = new PurchaseProductId(purchaseId, productId);
        purchaseProductService.deletePurchaseProduct(id);
        return ResponseEntity.ok("Se ha eliminado exitosamente.");
    }
}
