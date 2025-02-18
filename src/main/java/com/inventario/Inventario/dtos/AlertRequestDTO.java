package com.inventario.Inventario.dtos;

import com.inventario.Inventario.entities.AlertType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AlertRequestDTO {

    @NotBlank
    private Integer productId;

    @NotBlank
    private AlertType alertType;

    @NotBlank
    private LocalDateTime date;

}
