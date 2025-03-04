package com.inventario.Inventario.controllers;

import com.inventario.Inventario.dtos.DebtRequestDTO;
import com.inventario.Inventario.dtos.DebtResponseDTO;
import com.inventario.Inventario.dtos.PaymentRequestDTO;
import com.inventario.Inventario.services.DebtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/debts")
@RequiredArgsConstructor
public class DebtController {

    private final DebtService debtService;

  /*  @GetMapping()
    public List<DebtResponseDTO> getAllDebts(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction) {
        return debtService.getAllDebtsSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DebtResponseDTO> getDebtById(@PathVariable Integer id) {
        return ResponseEntity.ok(debtService.getDebtById(id));
    }

    @PostMapping
    public ResponseEntity<DebtResponseDTO> createDebt(@RequestBody DebtRequestDTO debtRequestDTO) {
        DebtResponseDTO newDebt = debtService.createDebt(debtRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDebt);
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<?> processDebtPayment(
            @PathVariable  Integer id,
            @RequestBody PaymentRequestDTO paymentRequest){
        DebtResponseDTO updated = debtService.processDebtPayment(id, paymentRequest.getAmount(), paymentRequest.getPaymentMethod());
        return ResponseEntity.ok(Objects.requireNonNullElse(updated,
                "La deuda ha sido completamente saldada."));
    }

    @GetMapping("/unpaid")
    public List<DebtResponseDTO> getAllUnpaidDebts(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction) {
        return debtService.getAllUnpaidDebts(sortBy, direction);
    }*/

    /*@PatchMapping("/update-debt-values/debt/{id}")
    public ResponseEntity<DebtResponseDTO> updateDebtValues(@PathVariable Integer id) {
        DebtResponseDTO updated = debtService.updateDebtValues(id);
        return ResponseEntity.ok(updated);
    }*/
}
