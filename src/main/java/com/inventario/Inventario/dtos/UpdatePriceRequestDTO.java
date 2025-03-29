package com.inventario.Inventario.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdatePriceRequestDTO {
    private BigDecimal newPrice;
}
