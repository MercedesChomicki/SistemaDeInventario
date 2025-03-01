package com.inventario.Inventario.dtos;

import com.inventario.Inventario.entities.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PaymentRequestDTO {
    private PaymentMethod paymentMethod;
    private BigDecimal amount = BigDecimal.ZERO;
}
