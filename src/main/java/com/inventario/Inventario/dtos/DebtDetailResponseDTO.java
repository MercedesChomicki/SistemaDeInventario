package com.inventario.Inventario.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DebtDetailResponseDTO {
    private Integer productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
