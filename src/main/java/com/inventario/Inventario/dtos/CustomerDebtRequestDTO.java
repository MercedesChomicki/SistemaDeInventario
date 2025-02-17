package com.inventario.Inventario.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CustomerDebtRequestDTO {

    @Schema(example = "1")
    @NotBlank
    private Integer customerId;

    @NotBlank
    private LocalDateTime date;

    @NotBlank
    private double totalAmount;

    @NotBlank
    private double amountPaid;
}
