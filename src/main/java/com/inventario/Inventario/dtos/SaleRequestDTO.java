package com.inventario.Inventario.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class SaleRequestDTO {
    @NotNull
    private Integer userId;
    @NotEmpty
    private List<PaymentRequestDTO> payments;
}
