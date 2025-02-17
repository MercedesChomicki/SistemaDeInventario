package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.CustomerDebtRequestDTO;
import com.inventario.Inventario.entities.CustomerDebt;
import com.inventario.Inventario.services.CustomerDebtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer_debts")
@RequiredArgsConstructor
public class CustomerDebtController {

    private final CustomerDebtService customerDebtService;

    @GetMapping()
    public List<CustomerDebt> getAllCustomerDebts(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return customerDebtService.getAllCustomerDebtsSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDebt> getCustomerDebtById(@PathVariable Integer id) {
        return ResponseEntity.ok(customerDebtService.getCustomerDebtById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerDebt> createCustomerDebt(@RequestBody CustomerDebtRequestDTO customerDebtRequestDTO) {
        CustomerDebt newCustomerDebt = customerDebtService.createCustomerDebt(customerDebtRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCustomerDebt);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDebt> updateCustomerDebt(@PathVariable Integer id, @RequestBody CustomerDebtRequestDTO updatedCustomerDebt) {
        CustomerDebt updated = customerDebtService.updateCustomerDebt(id, updatedCustomerDebt);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomerDebt(@PathVariable Integer id) {
        customerDebtService.deleteCustomerDebt(id);
        return ResponseEntity.ok("Se ha eliminado exitosamente.");
    }
}
