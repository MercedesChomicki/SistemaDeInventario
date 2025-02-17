package com.inventario.Inventario.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SupplierRequestDTO {

    @Schema(example = "José")
    @NotBlank
    private String firstname;

    @Schema(example = "Pérez")
    @NotBlank
    private String lastname;

    @Schema(example = "2494102030")
    @NotBlank
    private String phone;

    @Schema(example = "joseperez@gmail.com")
    @NotBlank
    private String email;

    @NotBlank
    private String company;

}
