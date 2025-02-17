package com.inventario.Inventario.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CartRequestDTO {

    @NotBlank
    private LocalDateTime creationDate;
}
