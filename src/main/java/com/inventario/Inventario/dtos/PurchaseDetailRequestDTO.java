package com.inventario.Inventario.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PurchaseDetailRequestDTO {
    @NotBlank
    private Integer productId;
    @NotBlank
    private Integer quantity;
    @NotBlank
    private BigDecimal price;
}
