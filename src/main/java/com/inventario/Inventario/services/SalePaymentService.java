package com.inventario.Inventario.services;

import com.inventario.Inventario.dtos.PaymentRequestDTO;
import com.inventario.Inventario.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static com.inventario.Inventario.constants.Constant.SURCHARGE_PERCENTAGE;

@Service
@RequiredArgsConstructor
public class SalePaymentService {

    public BigDecimal calculateSurcharge(BigDecimal amount) {
        return amount.multiply(SURCHARGE_PERCENTAGE)
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    }

    @Transactional
    public List<SalePayment> processSalePayments(Sale sale, List<PaymentRequestDTO> paymentsDTO, BigDecimal total) {
        validatePayments(paymentsDTO, total);

        return paymentsDTO.stream()
                .map(paymentDTO -> new SalePayment(paymentDTO.getPaymentMethod(), paymentDTO.getAmount(), sale))
                .collect(Collectors.toList());
    }

    private void validatePayments(List<PaymentRequestDTO> paymentsDTO, BigDecimal total) {
        if (paymentsDTO == null || paymentsDTO.isEmpty())
            throw new IllegalArgumentException("Debe haber al menos un pago registrado.");

        BigDecimal totalPayment = paymentsDTO.stream()
                .map(PaymentRequestDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPayment.compareTo(total) != 0)
            throw new IllegalArgumentException("El monto ingresado (" + totalPayment + ") no coincide con el total esperado (" + total + ").");
    }

    public BigDecimal calculateBaseAmount(BigDecimal amount) {
        BigDecimal surchargeRate = BigDecimal.ONE.add(
                SURCHARGE_PERCENTAGE.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)); // 1 + 0,07 = 1,07
        return amount.divide(surchargeRate, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalWithoutSurcharge(List<? extends TransactionDetail> details){
        return details.stream()
                .map(TransactionDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

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
}
