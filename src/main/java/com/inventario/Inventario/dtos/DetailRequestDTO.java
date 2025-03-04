package com.inventario.Inventario.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DetailRequestDTO {
    @NotBlank
    private Integer productId;
    @NotBlank
    private Integer quantity;
}
