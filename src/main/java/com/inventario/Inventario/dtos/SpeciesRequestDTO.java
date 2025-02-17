package com.inventario.Inventario.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class SpeciesRequestDTO {

    @Schema(example = "Pájaro")
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }
}
