package com.inventario.Inventario.services;

import com.inventario.Inventario.entities.Product;
import com.inventario.Inventario.exceptions.InsufficientStockException;
import com.inventario.Inventario.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductRepository productRepository;

    public void validateAndUpdateStock(Product product, int newQuantity, int oldQuantity) {
        int quantityChange = newQuantity - oldQuantity;

        if (quantityChange > 0 && product.getStock() < quantityChange) {
            throw new InsufficientStockException(
                    String.format("Stock insuficiente. Producto %s (ID %d). Disponible: %d",
                            product.getName(), product.getId(), product.getStock()));
        }

        product.setStock(product.getStock() - quantityChange);
        productRepository.save(product);
    }

    public void increaseStock(Product product, int quantity) {
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }
}
