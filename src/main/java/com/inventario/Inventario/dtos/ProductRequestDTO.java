package com.inventario.Inventario.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class ProductRequestDTO {
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @Positive
    private BigDecimal purchasePrice;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal percentageIncrease;
    @Min(0)
    private BigDecimal cashPrice;
    @Min(0)
    private int stock;
    @Schema(example = "null")
    @Pattern(regexp = "^(https?://.*)?$", message = "Debe ser una URL válida o estar vacío")
    private String imageUrl;
    @Future // Evita que alguien ingrese productos con fechas ya vencidas
    private LocalDate expirationDate;
    @NotNull
    private Integer speciesId;
    @NotNull
    private Integer categoryId;
    @NotNull
    private Integer supplierId;
}
