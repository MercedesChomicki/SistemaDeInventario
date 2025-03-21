package com.inventario.Inventario.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartUptRequestDTO {
    @NotNull
    private Integer userId;
}
