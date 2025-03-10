package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.UserRequestDTO;
import com.inventario.Inventario.entities.Admin;
import com.inventario.Inventario.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping()
    public List<Admin> getAllAdministrators(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return adminService.getAllAdministratorsSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Admin> getAdministratorById(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.getAdministratorById(id));
    }

    @PostMapping
    public ResponseEntity<Admin> createAdministrator(@RequestBody UserRequestDTO userRequestDTO) {
        Admin newAdmin = adminService.createAdministrator(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAdmin);
    }

    @Operation
    @PutMapping("/{id}")
    public ResponseEntity<Admin> updateAdministrator (@PathVariable Integer id, @RequestBody @Validated UserRequestDTO updatedAdministrator) {
        Admin updated = adminService.updateAdministrator(id, updatedAdministrator);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAdministrator(@PathVariable Integer id) {
        adminService.deleteAdministrator(id);
        return ResponseEntity.ok("Se ha eliminado exitosamente.");
    }
}
