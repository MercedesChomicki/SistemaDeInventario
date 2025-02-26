package com.inventario.Inventario.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class SaleRequestDTO {
    @NotEmpty
    private List<SaleDetailRequestDTO> details;
    @NotEmpty
    private List<SalePaymentRequestDTO> payments;
}
