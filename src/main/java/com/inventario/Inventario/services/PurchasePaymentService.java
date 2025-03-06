package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.PaymentRequestDTO;
import com.inventario.Inventario.entities.*;
import com.inventario.Inventario.exceptions.InvalidPaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchasePaymentService extends PaymentService {

    @Transactional
    public List<PurchasePayment> processPurchasePayments(Purchase purchase, List<PaymentRequestDTO> paymentsDTO, BigDecimal total) {
        validateSalePayments(paymentsDTO, total);

        return paymentsDTO.stream()
                .map(paymentDTO -> new PurchasePayment(paymentDTO.getPaymentMethod(), paymentDTO.getAmount(), purchase))
                .collect(Collectors.toList());
    }

    private void validateSalePayments(List<PaymentRequestDTO> paymentsDTO, BigDecimal total) {
        BigDecimal totalPayment = paymentsDTO.stream()
                .map(PaymentRequestDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPayment.compareTo(total) != 0)
            throw new InvalidPaymentException("El monto ingresado (" + totalPayment + ") no coincide con el total esperado (" + total + ").");
    }
}
