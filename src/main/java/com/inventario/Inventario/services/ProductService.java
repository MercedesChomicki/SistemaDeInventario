package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.ProductRequestDTO;
import com.inventario.Inventario.entities.Category;
import com.inventario.Inventario.entities.Product;
import com.inventario.Inventario.entities.Species;
import com.inventario.Inventario.entities.Supplier;
import com.inventario.Inventario.exceptions.BusinessException;
import com.inventario.Inventario.exceptions.NotFoundException;
import com.inventario.Inventario.repositories.CategoryRepository;
import com.inventario.Inventario.repositories.ProductRepository;
import com.inventario.Inventario.repositories.SpeciesRepository;
import com.inventario.Inventario.repositories.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SpeciesRepository speciesRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    /**
     * Obtener todos los productos con opción de ordenación
     **/
    public List<Product> getAllProductsSorted(String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortBy);
        return productRepository.findAll(sort);
    }

    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    public Product createProduct(ProductRequestDTO dto) {
        Species species = speciesRepository.findById(dto.getSpeciesId())
                .orElseThrow(() -> new RuntimeException("Species not found"));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        Product product = new Product();
        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setExpirationDate(dto.getExpirationDate());
        product.setImage(dto.getImage());
        product.setSpecies(species);
        product.setCategory(category);
        product.setSupplier(supplier);

        return productRepository.save(product);
    }

    public Optional<Product> updateProduct(Integer id, ProductRequestDTO updatedProduct) {
        return productRepository.findById(id).map(existingProduct -> {

            Species species = speciesRepository.findById(updatedProduct.getSpeciesId())
                    .orElseThrow(() -> new RuntimeException("Species not found"));
            Category category = categoryRepository.findById(updatedProduct.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            Supplier supplier = supplierRepository.findById(updatedProduct.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));

            // Copiar solo los valores no nulos del objeto actualizado
            if (updatedProduct.getName() != null) existingProduct.setName(updatedProduct.getName());
            if (updatedProduct.getCode() != null) existingProduct.setCode(updatedProduct.getCode());
            if (updatedProduct.getDescription() != null) existingProduct.setDescription(updatedProduct.getDescription());
            if (updatedProduct.getPrice() != null) existingProduct.setPrice(updatedProduct.getPrice());
            if (updatedProduct.getStock() > 0) existingProduct.setStock(updatedProduct.getStock());
            if (updatedProduct.getExpirationDate() != null) existingProduct.setExpirationDate(updatedProduct.getExpirationDate());
            if (updatedProduct.getImage() != null) existingProduct.setImage(updatedProduct.getImage());
            if (updatedProduct.getSpeciesId() != null) existingProduct.setSpecies(species);
            if (updatedProduct.getCategoryId() != null) existingProduct.setCategory(category);
            if (updatedProduct.getSupplierId() != null) existingProduct.setSupplier(supplier);

            return productRepository.save(existingProduct);
        });
    }

    public boolean deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException("No se encontró el producto con ID: " + id);
        }

        try {
            productRepository.deleteById(id);
            return true;
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("No se puede eliminar el producto porque tiene dependencias.");
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al eliminar el producto: " + e.getMessage());
        }
    }

    /** Como usuario quiero poder generar un reporte en cualquier momento
    para saber qué productos y qué cantidad de cada uno se va vendiendo en el día,
    con la posibilidad de ordenarlos de menor a mayor o de mayor a menor
    */
    public List<Object[]> getSalesReportByDate(LocalDate date, boolean asc) {
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(23, 59, 59);

        List<Object[]> results = productRepository.getSalesReportByDate(startDate, endDate);
        results.sort(Comparator.comparing(o -> (Integer) o[1])); // Ordena por cantidad vendida

        if (!asc) Collections.reverse(results); // Si quiere orden descendente, lo invierte

        return results;
    }
}

