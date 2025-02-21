package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.DebtRequestDTO;
import com.inventario.Inventario.dtos.DebtResponseDTO;
import com.inventario.Inventario.entities.Debt;
import com.inventario.Inventario.services.DebtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/debts")
@RequiredArgsConstructor
public class DebtController {

    private final DebtService debtService;

    @GetMapping()
    public List<DebtResponseDTO> getAllDebts(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return debtService.getAllDebtsSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Debt> getDebtById(@PathVariable Integer id) {
        return ResponseEntity.ok(debtService.getDebtById(id));
    }

    @PostMapping
    public ResponseEntity<Debt> createDebt(@RequestBody DebtRequestDTO debtRequestDTO) {
        Debt newDebt = debtService.createDebt(debtRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDebt);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Debt> updateDebt(@PathVariable Integer id, @RequestBody DebtRequestDTO updatedDebt) {
        Debt updated = debtService.updateDebt(id, updatedDebt);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/paydebt/{id}/incash/{incash}/amount/{amount}")
    public ResponseEntity<Debt> payDebt(
            @PathVariable  Integer id,
            @PathVariable boolean incash,
            @PathVariable BigDecimal amount){
        Debt updated = debtService.payDebt(id, incash, amount);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/paydebt/{id}/incash/{incash}/amount/{amount}")
    public ResponseEntity<Debt> payDebtWithSurcharge(
            @PathVariable  Integer id,
            @PathVariable boolean incash,
            @PathVariable BigDecimal amount){
        Debt updated = debtService.payDebtWithSurcharge(id, incash, amount);
        return ResponseEntity.ok(updated);
    }

    /*@DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDebt(@PathVariable Integer id) {
        debtService.deleteDebt(id);
        return ResponseEntity.ok("Se ha eliminado exitosamente.");
    }*/
}
