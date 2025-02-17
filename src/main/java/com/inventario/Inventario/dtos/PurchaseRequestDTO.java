package com.inventario.Inventario.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PurchaseRequestDTO {

    @Schema(example = "1")
    @NotNull
    private Integer supplierId;

    @NotNull
    private LocalDateTime date;
}
