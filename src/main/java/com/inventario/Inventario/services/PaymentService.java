package com.inventario.Inventario.services;

import com.inventario.Inventario.entities.Debt;
import com.inventario.Inventario.entities.DebtStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.inventario.Inventario.constants.Constant.SURCHARGE_PERCENTAGE;

@Service
@RequiredArgsConstructor
public class PaymentService {

    public BigDecimal calculateBaseAmount(BigDecimal amount) {
        BigDecimal surchargeRate = BigDecimal.ONE.add(
                SURCHARGE_PERCENTAGE.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)); // 1 + 0,07 = 1,07
        return amount.divide(surchargeRate, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateSurcharge(BigDecimal total) {
        return total.multiply(SURCHARGE_PERCENTAGE).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    }

    public void processPayment(Debt debt, BigDecimal amount, boolean incash) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a pagar debe ser mayor que cero.");
        }

        // Si el pago es con transferencia, volver al importe base
        BigDecimal baseAmount = amount, surcharge = BigDecimal.ZERO;
        if(!incash) {
            baseAmount = calculateBaseAmount(amount);
            surcharge = calculateSurcharge(baseAmount);
        }

        if (baseAmount.compareTo(debt.getAmountDue()) > 0) {
            throw new IllegalArgumentException("El monto a pagar no puede ser mayor a la deuda pendiente.");
        }

        // Si el pago es por transferencia, calcular recargo
        debt.setAmountTotal(debt.getAmountTotal().add(surcharge));
        debt.setAmountPaid(debt.getAmountPaid().add(amount));
        debt.setAmountDue(debt.getAmountTotal().subtract(debt.getAmountPaid()));

        // Determinar el nuevo estado de la deuda
        boolean isCompleted = debt.getAmountDue().compareTo(BigDecimal.ZERO) == 0;
        debt.setStatus(isCompleted ? DebtStatus.PAID : DebtStatus.PARTIALLY_PAID);
    }
}
