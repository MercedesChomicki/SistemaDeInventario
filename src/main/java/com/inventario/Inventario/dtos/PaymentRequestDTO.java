package com.inventario.Inventario.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor  // Crea un constructor vacío
@AllArgsConstructor // Crea un constructor con todos los atributos
@Getter
public class PaymentRequestDTO {
    private boolean isInCash;
    private BigDecimal amount = BigDecimal.ZERO;
}
