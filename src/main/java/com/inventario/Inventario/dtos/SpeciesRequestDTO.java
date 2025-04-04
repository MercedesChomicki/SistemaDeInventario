package com.inventario.Inventario.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SpeciesRequestDTO {
    @Schema(example = "Pájaro")
    @NotBlank
    private String name;
}
