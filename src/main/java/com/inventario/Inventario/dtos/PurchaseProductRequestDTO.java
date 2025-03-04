package com.inventario.Inventario.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PurchaseProductRequestDTO {

    @Schema(example = "1")
    @NotNull
    private Integer purchaseId;

    @Schema(example = "1")
    @NotNull
    private Integer productId;

    @Schema(example = "10")
    @NotNull
    @Min(0)
    private int quantity;

    @NotNull
    @Min(0)
    private double unitPrice;
}
