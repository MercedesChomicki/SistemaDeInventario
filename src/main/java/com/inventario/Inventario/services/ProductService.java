package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.ProductFullResponseDTO;
import com.inventario.Inventario.dtos.ProductRequestDTO;
import com.inventario.Inventario.dtos.ProductResponseDTO;
import com.inventario.Inventario.dtos.UpdatePriceRequestDTO;
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
        return productMapper.toDTO(findProductById(id));
    }

    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        validateProductUniqueness(dto.getCode(), dto.getName());
        validateExpirationDate(dto.getExpirationDate());

        RelatedEntities entities = fetchRelatedEntities(dto);

        Product product = new Product(
                dto.getCode(), dto.getName(), dto.getDescription(), dto.getCost(), dto.getSalePrice(),
                dto.getStock(), dto.getImageUrl(), dto.getExpirationDate(),
                entities.species(), entities.category(), entities.supplier()
        );

        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    public ProductFullResponseDTO updateProduct(Integer id, ProductRequestDTO dto) {
        Product product = findProductById(id);

        /* TODO Si dejo nulos los campos specieId, categoryId y supplierId da error.
            Solución: generar autocompletado en el front-end para que queden los valores
            anteriores en caso de no querer modificarlos */
        RelatedEntities entities = fetchRelatedEntities(dto);

        copyNonNullProperties(dto, product);

        if (dto.getSpeciesId() != null) product.setSpecies(entities.species());
        if (dto.getCategoryId() != null) product.setCategory(entities.category());
        if (dto.getSupplierId() != null) product.setSupplier(entities.supplier());

        Product savedProduct = productRepository.save(product);
        return productMapper.toFullDTO(savedProduct);
    }

    public void updatePrice(Integer id, UpdatePriceRequestDTO dto) {
        if (dto.getNewPrice() == null || dto.getNewPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El precio debe ser mayor que cero.");
        }
        Product product = findProductById(id);
        product.setSalePrice(dto.getNewPrice());
        productRepository.save(product);
    }

    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto", id);
        }
        productRepository.deleteById(id);
    }

    private Product findProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
    }

    private void copyNonNullProperties(ProductRequestDTO dto, Product product) {
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getCode() != null) product.setCode(dto.getCode());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getImageUrl() != null) product.setImageUrl(dto.getImageUrl());
        if (dto.getCost() != null) product.setCost(dto.getCost());
        if (dto.getSalePrice() != null && dto.getSalePrice().compareTo(BigDecimal.ZERO) > 0)
            product.setSalePrice(dto.getSalePrice());
    }

    private void validateExpirationDate(LocalDate expirationDate) {
        if (expirationDate != null && expirationDate.isBefore(LocalDate.now())) {
            throw new BusinessException("La fecha de expiración no puede estar en el pasado.");
        }
    }

    private void validateProductUniqueness(String code, String name) {
        boolean exists = productRepository.existsByCodeIgnoreCase(code) || productRepository.existsByNameIgnoreCase(name);
        if (exists) {
            throw new BusinessException("El producto ya se encuentra registrado");
        }
    }

    private record RelatedEntities(Species species, Category category, Supplier supplier) {}

    private RelatedEntities fetchRelatedEntities(ProductRequestDTO dto) {
        return new RelatedEntities(
                speciesRepository.findById(dto.getSpeciesId())
                        .orElseThrow(() -> new ResourceNotFoundException("Especie", dto.getSpeciesId())),
                categoryRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Categoría", dto.getCategoryId())),
                supplierRepository.findById(dto.getSupplierId())
                        .orElseThrow(() -> new ResourceNotFoundException("Proveedor", dto.getSupplierId()))
        );
    }
}

