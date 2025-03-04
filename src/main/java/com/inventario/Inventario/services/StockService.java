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

    public void validateStock(Product p) {
        if (p.getStock() == 0) {
            throw new InsufficientStockException(
                    String.format("Producto %s de ID %d, sin stock", p.getName(), p.getId()));
        }
    }

    public void validateStock(Product p, Integer quantity) {
        if (p.getStock() < quantity) {
            throw new InsufficientStockException(
                    String.format("Stock insuficiente. Producto %s de ID %d. Disponible: %d", p.getName(), p.getId(), p.getStock()));
        }
    }

    public void increaseAndSaveStock(Product p) {
        p.setStock(p.getStock()+1);
        productRepository.save(p);
    }

    public void decreaseAndSaveStock(Product p) {
        p.setStock(p.getStock() - 1);
        productRepository.save(p);
    }

    public void updateAndSaveStock(Product p, Integer quantity, Integer selectedQuantity) {
        int q = quantity - selectedQuantity;
        p.setStock(p.getStock()-q);
        productRepository.save(p);
    }

    /*public Map<Integer, Product> validateAndUpdateStock(List<DetailRequestDTO> details) {
        // Obtener todos los productId del detalle en una sola consulta
        Set<Integer> productIds = details.stream()
                .map(DetailRequestDTO::getProductId)
                .collect(Collectors.toSet());

        // Traer todos los productos en una sola consulta
        List<Product> products = productRepository.findAllById(productIds);
        // Mapear cada producto con su respectivo id
        Map<Integer, Product> productsMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        for (DetailRequestDTO detailDTO : details) {
            Product product = productsMap.get(detailDTO.getProductId());
            if(product == null) {
                throw new IllegalArgumentException(
                        String.format("El producto con id %d no se encuentra en el sistema", detailDTO.getProductId()));
            }
            if (product.getStock() < detailDTO.getQuantity()) {
                throw new IllegalArgumentException(
                        String.format("Stock insuficiente para %s (ID: %d). Disponible: %d",
                                product.getName(), product.getId(), product.getStock()));
            }
            product.setStock(product.getStock() - detailDTO.getQuantity());
        }
        productRepository.saveAll(products);
        return productsMap;
    }*/
}
