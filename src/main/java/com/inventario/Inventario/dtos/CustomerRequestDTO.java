package com.inventario.Inventario.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CustomerRequestDTO {

    @Schema(example = "Fernando")
    @NotBlank
    private String firstname;

    @Schema(example = "Rodriguez")
    @NotBlank
    private String lastname;

    private String phone;

}
