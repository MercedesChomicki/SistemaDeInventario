package com.inventario.Inventario.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class SaleRequestDTO {
    @NotEmpty
    private List<DetailRequestDTO> details;
    @NotEmpty
    private List<PaymentRequestDTO> payments;
}
