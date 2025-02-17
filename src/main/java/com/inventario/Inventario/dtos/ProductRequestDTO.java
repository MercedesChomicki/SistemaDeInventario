package com.inventario.Inventario.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class ProductRequestDTO {

    @Schema(example = "PIP-001")
    @NotBlank
    private String code;

    @Schema(example = "Frontline gato común")
    @NotBlank
    private String name;

    private String description;

    @Schema(example = "6000")
    @NotNull
    @Positive
    private BigDecimal price;

    @Schema(example = "10")
    @NotNull
    @Min(0)
    private int stock;

    @Schema(example = "2026-02-15")
    private LocalDate expirationDate;

    @Schema(example = "null")
    private String imageUrl;

    @Schema(example = "1")
    @NotNull
    private Integer speciesId;

    @Schema(example = "1")
    @NotNull
    private Integer categoryId;

    @Schema(example = "1")
    @NotNull
    private Integer supplierId;
}
