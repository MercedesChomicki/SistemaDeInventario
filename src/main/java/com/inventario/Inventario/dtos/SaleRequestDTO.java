package com.inventario.Inventario.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SaleRequestDTO {
    @NotNull
    private Integer userId;
    @NotEmpty
    private List<PaymentRequestDTO> payments;
}
