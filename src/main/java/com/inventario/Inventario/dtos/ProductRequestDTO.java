package com.inventario.Inventario.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductRequestDTO {
    private String code;
    private String name;
    private String description;
    @Positive
    private BigDecimal purchasePrice;
    @DecimalMin("0.0")
    private BigDecimal percentageIncrease;
    @Min(0)
    private BigDecimal cashPrice;
    @Min(0)
    private int stock;
    @Schema(example = "null")
    /*@Pattern(regexp = "^(https?://.*)?$", message = "Debe ser una URL válida o estar vacío")*/
    private String imageUrl;
    @Future // Evita que alguien ingrese productos con fechas ya vencidas
    private LocalDate expirationDate;
    private Integer speciesId;
    private Integer categoryId;
    private Integer supplierId;
}
