package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.PaymentRequestDTO;
import com.inventario.Inventario.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalePaymentService extends PaymentService {

    // TODO Si PaymentMethod = CARD o TRANSFER, amount viene con el recargo desde el front
    public BigDecimal calculateTotal(List<? extends TransactionDetail> details, List<PaymentRequestDTO> payments){
        BigDecimal totalWithoutSurcharge  = calculateTotalWithoutSurcharge(details);

        BigDecimal totalSurcharge = BigDecimal.ZERO;
        for(PaymentRequestDTO p : payments) {
            if(p.getPaymentMethod() != PaymentMethod.CASH) {
                BigDecimal baseAmount = calculateBaseAmount(p.getAmount());
                BigDecimal surcharge =  calculateSurcharge(baseAmount);
                totalSurcharge = totalSurcharge.add(surcharge);
            }
        }
        return totalWithoutSurcharge.add(totalSurcharge);
    }

    @Transactional
    public List<SalePayment> processSalePayments(Sale sale, List<PaymentRequestDTO> paymentsDTO, BigDecimal total) {
        validateSalePayments(paymentsDTO, total);

        return paymentsDTO.stream()
                .map(paymentDTO -> new SalePayment(paymentDTO.getPaymentMethod(), paymentDTO.getAmount(), sale))
                .collect(Collectors.toList());
    }

    private void validateSalePayments(List<PaymentRequestDTO> paymentsDTO, BigDecimal total) {
        BigDecimal totalPayment = paymentsDTO.stream()
                .map(PaymentRequestDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPayment.compareTo(total) != 0)
            throw new IllegalArgumentException("El monto ingresado (" + totalPayment + ") no coincide con el total esperado (" + total + ").");
    }
}
