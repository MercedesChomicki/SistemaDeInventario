package com.inventario.Inventario.services;

import com.inventario.Inventario.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.inventario.Inventario.constants.Constant.SURCHARGE_PERCENTAGE;

@Service
@RequiredArgsConstructor
public class DebtPaymentService {

    public BigDecimal calculateBaseAmount(BigDecimal amount) {
        BigDecimal surchargeRate = BigDecimal.ONE.add(
                SURCHARGE_PERCENTAGE.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)); // 1 + 0,07 = 1,07
        return amount.divide(surchargeRate, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateSurcharge(BigDecimal amount) {
        return amount.multiply(SURCHARGE_PERCENTAGE)
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalWithoutSurcharge(List<? extends TransactionDetail> details){
        return details.stream()
                .map(TransactionDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void processPayment(Debt debt, BigDecimal amount, PaymentMethod paymentMethod) {
        validatePayments(paymentMethod, amount);

        // Si el pago es con transferencia, volver al importe base para calcular el surcharge
        BigDecimal baseAmount = (paymentMethod == PaymentMethod.CASH) ? amount : calculateBaseAmount(amount);
        BigDecimal surcharge = (paymentMethod == PaymentMethod.CASH) ? BigDecimal.ZERO : calculateSurcharge(baseAmount);

        if (baseAmount.compareTo(debt.getAmountDue()) > 0)
            throw new IllegalArgumentException("El monto a pagar no puede ser mayor a la deuda pendiente.");

        debt.recalculateDebt(null, amount, surcharge);
    }

    private void validatePayments(PaymentMethod paymentMethod, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a pagar debe ser mayor que cero.");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Debe haber al menos un pago registrado.");
        }
    }
}
