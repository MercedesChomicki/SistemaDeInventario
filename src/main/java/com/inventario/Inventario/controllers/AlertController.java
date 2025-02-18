package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.AlertRequestDTO;
import com.inventario.Inventario.entities.Alert;
import com.inventario.Inventario.services.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping()
    public List<Alert> getAllAlerts(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return alertService.getAllAlertsSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlertById(@PathVariable Integer id) {
        return ResponseEntity.ok(alertService.getAlertById(id));
    }

    @PostMapping
    public ResponseEntity<Alert> createAlert(@RequestBody AlertRequestDTO alertRequestDTO) {
        Alert newAlert = alertService.createAlert(alertRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAlert);
    }

    @Operation
    @PutMapping("/{id}")
    public ResponseEntity<Alert> updateAlert (@PathVariable Integer id, @RequestBody @Validated AlertRequestDTO updatedAlert) {
        Alert updated = alertService.updateAlert(id, updatedAlert);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAlert(@PathVariable Integer id) {
        alertService.deleteAlert(id);
        return ResponseEntity.ok("Se ha eliminado exitosamente.");
    }
}
