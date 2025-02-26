package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.DebtDetailRequestDTO;
import com.inventario.Inventario.entities.Product;
import com.inventario.Inventario.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductRepository productRepository;

    public Map<Integer, Product> validateAndUpdateStock(List<DebtDetailRequestDTO> details) {
        if (details == null || details.isEmpty()) {
            throw new IllegalArgumentException("No se puede crear una deuda sin productos");
        }

        // Obtener todos los IDs de productos en una sola consulta
        Set<Integer> productIds = details.stream()
                .map(DebtDetailRequestDTO::getProductId)
                .collect(Collectors.toSet());

        // Traer todos los productos en una sola consulta
        List<Product> products = productRepository.findAllById(productIds);
        Map<Integer, Product> productsMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // Validar que todos los productos existan
        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException("Algunos productos no se encuentran en el sistema.");
        }

        for (DebtDetailRequestDTO detailDTO : details) {
            Integer productId = detailDTO.getProductId();
            Integer quantity = detailDTO.getQuantity();

            Product product = productsMap.get(productId);
            if (product.getStock() < quantity) {
                throw new IllegalArgumentException("Stock insuficiente para " + product.getName());
            }

            // Descontar stock en memoria (Hibernate se encargará de actualizarlo en la BD)
            product.setStock(product.getStock() - quantity);
        }

        return productsMap;
    }
}
