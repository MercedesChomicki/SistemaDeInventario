package com.inventario.Inventario.services;

import com.inventario.Inventario.entities.Product;
import com.inventario.Inventario.repositories.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Obtener todos los productos con opción de ordenación
     **/
    public List<Product> getAllProductsSorted(String sortBy, String direction) {
        // Valores por defecto
        String defaultSortBy = "name";
        String defaultDirection = "asc";

        // Si sortBy es nulo o vacío, usar el valor por defecto
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = defaultSortBy;
        }

        // Si direction es nulo o inválido, se usa el valor por defecto
        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException | NullPointerException e) {
            sortDirection = Sort.Direction.fromString(defaultDirection);
        }

        // Construir el objeto de ordenación
        Sort sort = Sort.by(sortDirection, sortBy);
        return productRepository.findAll(sort);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Obtener un producto por ID
     */
    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    /**
     * Guardar un nuevo producto
     */
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Actualizar un producto existente
     */
    public Optional<Product> updateProduct(Integer id, Product updatedProduct) {
        return productRepository.findById(id).map(existingProduct -> {
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setCode(updatedProduct.getCode());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setStock(updatedProduct.getStock());
            existingProduct.setExpirationDate(updatedProduct.getExpirationDate());
            existingProduct.setImage(updatedProduct.getImage());
            existingProduct.setSpecies(updatedProduct.getSpecies());
            existingProduct.setCategory(updatedProduct.getCategory());
            existingProduct.setSupplier(updatedProduct.getSupplier());
            return productRepository.save(existingProduct);
        });
    }

    /**
     * Eliminar un producto por ID
     */
    public boolean deleteProduct(Integer id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }



}

