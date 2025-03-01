package com.inventario.Inventario.services;

import com.inventario.Inventario.entities.TransactionDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public BigDecimal calculateTotalWithoutSurcharge(List<? extends TransactionDetail> details){
        return details.stream()
                .map(TransactionDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
