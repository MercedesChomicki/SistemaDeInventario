package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.ProductFullResponseDTO;
import com.inventario.Inventario.dtos.ProductRequestDTO;
import com.inventario.Inventario.dtos.ProductResponseDTO;
import com.inventario.Inventario.entities.Category;
import com.inventario.Inventario.entities.Product;
import com.inventario.Inventario.entities.Species;
import com.inventario.Inventario.entities.Supplier;
import com.inventario.Inventario.exceptions.BusinessException;
import com.inventario.Inventario.exceptions.ResourceNotFoundException;
import com.inventario.Inventario.mappers.ProductMapper;
import com.inventario.Inventario.repositories.CategoryRepository;
import com.inventario.Inventario.repositories.ProductRepository;
import com.inventario.Inventario.repositories.SpeciesRepository;
import com.inventario.Inventario.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SpeciesRepository speciesRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final ProductMapper productMapper;

    public List<ProductResponseDTO> getAllProductsSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return productRepository.findAll(sort)
                .stream()
                .map(productMapper::toDTO)
                .toList();
    }

    public ProductResponseDTO getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        return productMapper.toDTO(product);
    }

    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        boolean exists = productRepository.existsByCodeIgnoreCase(dto.getCode())
                || productRepository.existsByNameIgnoreCase(dto.getName());

        if (exists)
            throw new BusinessException("El producto ya se encuentra registrado");

        Map<String, Object> entities = fetchRelatedEntities(dto);

        Product product = new Product(
                dto.getCode(), dto.getName(), dto.getDescription(), dto.getPurchasePrice(),
                dto.getPercentageIncrease(), dto.getCashPrice(), dto.getStock(), dto.getImageUrl(),
                dto.getExpirationDate(), (Species) entities.get("species"), (Category) entities.get("category"),
                (Supplier) entities.get("supplier")
        );

        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    public ProductFullResponseDTO updateProduct(Integer id, ProductRequestDTO updatedProduct) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        Map<String, Object> entities = fetchRelatedEntities(updatedProduct);

        // Copiar solo los valores no nulos del objeto actualizado
        if (updatedProduct.getName() != null) product.setName(updatedProduct.getName());
        if (updatedProduct.getCode() != null) product.setCode(updatedProduct.getCode());
        if (updatedProduct.getDescription() != null) product.setDescription(updatedProduct.getDescription());
        if (updatedProduct.getImageUrl() != null) product.setImageUrl(updatedProduct.getImageUrl());
        if (updatedProduct.getPurchasePrice() != null) product.setPurchasePrice(updatedProduct.getPurchasePrice());
        if (updatedProduct.getPercentageIncrease() != null) product.setPercentageIncrease(updatedProduct.getPercentageIncrease());
        if (updatedProduct.getCashPrice().compareTo(BigDecimal.ZERO) > 0) product.setCashPrice(updatedProduct.getCashPrice());

        if (updatedProduct.getStock() <= 0) throw new BusinessException("El stock debe ser mayor a 0.");
        else product.setStock(updatedProduct.getStock());

        if (updatedProduct.getExpirationDate() != null) {
            if (updatedProduct.getExpirationDate().isBefore(LocalDate.now())) {
                throw new BusinessException("La fecha de expiración no puede estar en el pasado.");
            }
            product.setExpirationDate(updatedProduct.getExpirationDate());
        }
        if (updatedProduct.getSpeciesId() != null) product.setSpecies((Species) entities.get("species"));
        if (updatedProduct.getCategoryId() != null) product.setCategory((Category) entities.get("category"));
        if (updatedProduct.getSupplierId() != null) product.setSupplier((Supplier) entities.get("supplier"));

        Product savedProduct = productRepository.save(product);
        return productMapper.toFullDTO(savedProduct);
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

    public ProductResponseDTO increaseStock(Integer id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        product.setStock(product.getStock() + quantity);
        Product updated = productRepository.save(product);
        return productMapper.toDTO(updated);
    }

    public ProductResponseDTO increasePrice(Integer id, BigDecimal newPrice) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        if (newPrice != null) product.setCashPrice(newPrice);
        Product updated = productRepository.save(product);
        return productMapper.toDTO(updated);
    }

    public ProductResponseDTO increasePriceWithPercentage(Integer id, BigDecimal percentage) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        if(percentage != null) {
            BigDecimal percentageIncrease = BigDecimal.ONE.add(percentage.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            product.setCashPrice(product.getCashPrice().multiply(percentageIncrease));
        }
        Product updated = productRepository.save(product);
        return productMapper.toDTO(updated);
    }
}

