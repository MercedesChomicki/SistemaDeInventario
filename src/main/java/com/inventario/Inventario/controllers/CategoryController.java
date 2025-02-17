package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.CategoryRequestDTO;
import com.inventario.Inventario.entities.Category;
import com.inventario.Inventario.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Obtener todas las categorias con opción de ordenación
     **/
    @GetMapping()
    public List<Category> getAllCategories(
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return categoryService.getAllCategoriesSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody CategoryRequestDTO categoryRequestDTO) {
        Category newCategory = categoryService.createCategory(categoryRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Integer id, @RequestBody CategoryRequestDTO updatedCategory) {
        Category updated = categoryService.updateCategory(id, updatedCategory);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Se ha eliminado exitosamente.");
    }
}
