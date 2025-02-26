package com.inventario.Inventario.dtos;

import com.inventario.Inventario.entities.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalePaymentRequestDTO {
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
}

