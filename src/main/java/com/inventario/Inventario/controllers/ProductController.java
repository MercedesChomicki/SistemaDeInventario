package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.ProductFullResponseDTO;
import com.inventario.Inventario.dtos.ProductRequestDTO;
import com.inventario.Inventario.dtos.ProductResponseDTO;
import com.inventario.Inventario.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    /**
     * Obtener todos los productos con opción de ordenación
     **/
    @GetMapping()
    public List<ProductResponseDTO> getAllProducts(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return productService.getAllProductsSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO newProduct = productService.createProduct(productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductFullResponseDTO> updateProduct(@PathVariable Integer id, @Valid @RequestBody ProductRequestDTO updatedProduct) {
        ProductFullResponseDTO updated = productService.updateProduct(id, updatedProduct);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Se ha eliminado exitosamente.");
    }

    @PatchMapping("/increase-stock/product/{id}/quantity/{quantity}")
    public ResponseEntity<ProductResponseDTO> increaseStock(
            @PathVariable Integer id,
            @PathVariable Integer quantity) {
        ProductResponseDTO updated = productService.increaseStock(id, quantity);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/increase-price/product/{id}/newPrice/{newPrice}")
    public ResponseEntity<ProductResponseDTO> increasePrice(
            @PathVariable Integer id,
            @PathVariable BigDecimal newPrice) {
        ProductResponseDTO updated = productService.increasePrice(id, newPrice);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/increase-price-w-percentage/product/{id}/percentaje/{percentaje}")
    public ResponseEntity<ProductResponseDTO> increasePriceWithPercentage(
            @PathVariable Integer id,
            @PathVariable BigDecimal percentaje) {
        ProductResponseDTO updated = productService.increasePriceWithPercentage(id, percentaje);
        return ResponseEntity.ok(updated);
    }

}

