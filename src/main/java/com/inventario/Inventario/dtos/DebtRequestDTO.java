package com.inventario.Inventario.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DebtRequestDTO {
    @NotNull
    private Integer userId;
    @NotNull
    private Integer customerId;
    private PaymentRequestDTO payment;
}
