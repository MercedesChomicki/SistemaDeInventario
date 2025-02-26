package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.ProductRequestDTO;
import com.inventario.Inventario.dtos.ProductResponseDTO;
import com.inventario.Inventario.entities.Product;
import com.inventario.Inventario.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
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
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        Product newProduct = productService.createProduct(productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestBody ProductRequestDTO updatedProduct) {
        Product updated = productService.updateProduct(id, updatedProduct);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Se ha eliminado exitosamente.");
    }

    @PatchMapping("/{id}/quantity/{quantity}")
    public ResponseEntity<ProductResponseDTO> increaseStock(
            @PathVariable Integer id,
            @PathVariable Integer quantity) {
        ProductResponseDTO updated = productService.increaseStock(id, quantity);
        return ResponseEntity.ok(updated);
    }

}

