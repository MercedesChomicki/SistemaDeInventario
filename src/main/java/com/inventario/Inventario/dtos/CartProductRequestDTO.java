package com.inventario.Inventario.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CartProductRequestDTO {

    @NotBlank
    private Integer productId;

    @NotBlank
    private int quantity;

    @NotBlank
    private double price;
}
