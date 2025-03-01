package com.inventario.Inventario.services;

import com.inventario.Inventario.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DebtPaymentService extends PaymentService{

    @Transactional
    public void processDebtPayment(Debt debt, BigDecimal amount, PaymentMethod paymentMethod) {
        validateDebtPayments(paymentMethod, amount);

        // Si el pago es con transferencia, volver al importe base para calcular el surcharge
        BigDecimal baseAmount = (paymentMethod == PaymentMethod.CASH) ? amount : calculateBaseAmount(amount);
        BigDecimal surcharge = (paymentMethod == PaymentMethod.CASH) ? BigDecimal.ZERO : calculateSurcharge(baseAmount);

        if (baseAmount.compareTo(debt.getAmountDue()) > 0)
            throw new IllegalArgumentException("El monto a pagar no puede ser mayor a la deuda pendiente.");

        debt.recalculateDebt(null, amount, surcharge);
    }

    private void validateDebtPayments(PaymentMethod paymentMethod, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a pagar debe ser mayor que cero.");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Debe haber al menos un pago registrado.");
        }
    }
}
