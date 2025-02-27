package com.inventario.Inventario.services;

import com.inventario.Inventario.entities.Debt;
import com.inventario.Inventario.entities.DebtDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.inventario.Inventario.constants.Constant.SURCHARGE_PERCENTAGE;

@Service
@RequiredArgsConstructor
public class PaymentService {

    public BigDecimal calculateBaseAmount(BigDecimal amount) {
        BigDecimal surchargeRate = BigDecimal.ONE.add(
                SURCHARGE_PERCENTAGE.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)); // 1 + 0,07 = 1,07
        return amount.divide(surchargeRate, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateSurcharge(BigDecimal amount) {
        return amount.multiply(SURCHARGE_PERCENTAGE)
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    }

    @Transactional
    public void processPayment(Debt debt, BigDecimal amount, boolean isInCash) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a pagar debe ser mayor que cero.");
        }

        // Si el pago es con transferencia, volver al importe base
        BigDecimal baseAmount = amount, surcharge = BigDecimal.ZERO;
        if(!isInCash) {
            baseAmount = calculateBaseAmount(amount);
            surcharge = calculateSurcharge(baseAmount);
        }

        System.out.println("SURCHARGE: "+surcharge);

        if (baseAmount.compareTo(debt.getAmountDue()) > 0) {
            throw new IllegalArgumentException("El monto a pagar no puede ser mayor a la deuda pendiente.");
        }
        debt.recalculateDebt(null, amount, surcharge);
    }

    public BigDecimal calculateTotal(List<DebtDetail> details){
        return details.stream()
                .map(DebtDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
