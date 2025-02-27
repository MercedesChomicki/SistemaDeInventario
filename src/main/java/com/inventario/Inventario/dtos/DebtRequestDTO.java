package com.inventario.Inventario.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor  // Crea un constructor vacío
@AllArgsConstructor // Crea un constructor con todos los atributos
@Getter
public class DebtRequestDTO {
    @Schema(example = "1")
    @NotNull
    private Integer customerId;
    @NotEmpty
    private List<DebtDetailRequestDTO> details;
    private BigDecimal amount = BigDecimal.ZERO;
    private boolean incash = true;
    private LocalDateTime date;
}
