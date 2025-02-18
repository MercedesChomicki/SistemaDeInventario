package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.ProductRequestDTO;
import com.inventario.Inventario.entities.Category;
import com.inventario.Inventario.entities.Product;
import com.inventario.Inventario.entities.Species;
import com.inventario.Inventario.entities.Supplier;
import com.inventario.Inventario.exceptions.BusinessException;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.repositories.CategoryRepository;
import com.inventario.Inventario.repositories.ProductRepository;
import com.inventario.Inventario.repositories.SpeciesRepository;
import com.inventario.Inventario.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SpeciesRepository speciesRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    /**
     * Obtener todos los productos con opción de ordenación
     **/
    public List<Product> getAllProductsSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return productRepository.findAll(sort);
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
    }

    public Product createProduct(ProductRequestDTO dto) {
        Map<String, Object> entities = fetchRelatedEntities(dto);

        Product product = new Product();
        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setExpirationDate(dto.getExpirationDate());
        product.setImageUrl(dto.getImageUrl());
        product.setSpecies((Species) entities.get("species"));
        product.setCategory((Category) entities.get("category"));
        product.setSupplier((Supplier) entities.get("supplier"));

        return productRepository.save(product);
    }

    public Product updateProduct(Integer id, ProductRequestDTO updatedProduct) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        Map<String, Object> entities = fetchRelatedEntities(updatedProduct);

        // Copiar solo los valores no nulos del objeto actualizado
        if (updatedProduct.getName() != null) existingProduct.setName(updatedProduct.getName());
        if (updatedProduct.getCode() != null) existingProduct.setCode(updatedProduct.getCode());
        if (updatedProduct.getDescription() != null) existingProduct.setDescription(updatedProduct.getDescription());
        if (updatedProduct.getPrice() != null) existingProduct.setPrice(updatedProduct.getPrice());

        if (updatedProduct.getStock() <= 0) throw new BusinessException("El stock debe ser mayor a 0.");
        else existingProduct.setStock(updatedProduct.getStock());

        if (updatedProduct.getExpirationDate() != null) existingProduct.setExpirationDate(updatedProduct.getExpirationDate());
        if (updatedProduct.getImageUrl() != null) existingProduct.setImageUrl(updatedProduct.getImageUrl());
        if (updatedProduct.getSpeciesId() != null) existingProduct.setSpecies((Species) entities.get("species"));
        if (updatedProduct.getCategoryId() != null) existingProduct.setCategory((Category) entities.get("category"));
        if (updatedProduct.getSupplierId() != null) existingProduct.setSupplier((Supplier) entities.get("supplier"));

        return productRepository.save(existingProduct);
    }

    private Map<String, Object> fetchRelatedEntities(ProductRequestDTO dto) {
        Species species = speciesRepository.findById(dto.getSpeciesId())
                .orElseThrow(() -> new ResourceNotFoundException("Especie", dto.getSpeciesId()));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", dto.getCategoryId()));
        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", dto.getSupplierId()));

        Map<String, Object> entities = new HashMap<>();
        entities.put("species", species);
        entities.put("category", category);
        entities.put("supplier", supplier);
        return entities;
    }


    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto", id);
        }
        productRepository.deleteById(id);
    }
}

