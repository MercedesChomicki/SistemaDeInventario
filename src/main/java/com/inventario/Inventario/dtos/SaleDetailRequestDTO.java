package com.inventario.Inventario.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SaleDetailRequestDTO {

    @NotBlank
    private Integer productId;

    @NotBlank
    private Integer quantity;

}
