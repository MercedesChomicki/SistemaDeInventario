package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.CustomerRequestDTO;
import com.inventario.Inventario.entities.Customer;
import com.inventario.Inventario.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping()
    public List<Customer> getAllCustomers(
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return customerService.getAllCustomersSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Integer id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerRequestDTO customerRequestDTO) {
        Customer newCustomer = customerService.createCustomer(customerRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCustomer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Integer id, @RequestBody CustomerRequestDTO updatedCustomer) {
        Customer updated = customerService.updateCustomer(id, updatedCustomer);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Integer id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Se ha eliminado exitosamente.");
    }
}
