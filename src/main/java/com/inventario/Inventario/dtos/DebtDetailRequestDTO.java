package com.inventario.Inventario.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DebtDetailRequestDTO {
    @NotBlank
    private Integer productId;
    @NotBlank
    private Integer quantity;
}
