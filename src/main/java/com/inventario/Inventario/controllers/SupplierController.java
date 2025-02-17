package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.SupplierRequestDTO;
import com.inventario.Inventario.entities.Supplier;
import com.inventario.Inventario.services.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping()
    public List<Supplier> getAllSuppliers(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return supplierService.getAllSuppliersSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Integer id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@RequestBody SupplierRequestDTO supplierRequestDTO) {
        Supplier newSupplier = supplierService.createSupplier(supplierRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSupplier);
    }

    @Operation
    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier (@PathVariable Integer id, @RequestBody @Validated SupplierRequestDTO updatedSupplier) {
        Supplier updated = supplierService.updateSupplier(id, updatedSupplier);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSupplier(@PathVariable Integer id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok("Se ha eliminado exitosamente.");
    }
}
