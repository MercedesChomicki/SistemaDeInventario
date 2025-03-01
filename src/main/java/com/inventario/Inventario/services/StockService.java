package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.DetailRequestDTO;
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

    public Map<Integer, Product> validateAndUpdateStock(List<DetailRequestDTO> details) {
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
    }
}
