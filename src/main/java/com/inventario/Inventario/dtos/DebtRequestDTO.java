package com.inventario.Inventario.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DebtRequestDTO {
    @Schema(example = "1")
    @NotNull
    private Integer customerId;
    @NotEmpty
    private List<DetailRequestDTO> details;
    private PaymentRequestDTO payment;
    private LocalDateTime date;
}
