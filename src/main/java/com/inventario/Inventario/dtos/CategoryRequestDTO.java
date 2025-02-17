package com.inventario.Inventario.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class CategoryRequestDTO {

    @Schema(example = "Alimentos")
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }
}
