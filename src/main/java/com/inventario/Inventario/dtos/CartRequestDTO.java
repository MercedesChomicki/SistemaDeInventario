package com.inventario.Inventario.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CartRequestDTO {
    @NotNull
    private Integer userId;
    @NotNull
    private Integer productId;
    @NotNull
    private Integer quantity;
}
