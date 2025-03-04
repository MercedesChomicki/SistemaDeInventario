package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.UserRequestDTO;
import com.inventario.Inventario.entities.Administrator;
import com.inventario.Inventario.services.AdministratorService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/administrators")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService administratorService;

    @GetMapping()
    public List<Administrator> getAllAdministrators(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return administratorService.getAllAdministratorsSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrator> getAdministratorById(@PathVariable Integer id) {
        return ResponseEntity.ok(administratorService.getAdministratorById(id));
    }

    @PostMapping
    public ResponseEntity<Administrator> createAdministrator(@RequestBody UserRequestDTO userRequestDTO) {
        Administrator newAdministrator = administratorService.createAdministrator(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAdministrator);
    }

    @Operation
    @PutMapping("/{id}")
    public ResponseEntity<Administrator> updateAdministrator (@PathVariable Integer id, @RequestBody @Validated UserRequestDTO updatedAdministrator) {
        Administrator updated = administratorService.updateAdministrator(id, updatedAdministrator);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAdministrator(@PathVariable Integer id) {
        administratorService.deleteAdministrator(id);
        return ResponseEntity.ok("Se ha eliminado exitosamente.");
    }
}
