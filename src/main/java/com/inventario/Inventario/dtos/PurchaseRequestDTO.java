package com.inventario.Inventario.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class PurchaseRequestDTO {
    @NotNull
    private Integer supplierId;

    @NotEmpty(message = "Debe haber al menos un producto en la compra")
    private List<@Valid PurchaseDetailRequestDTO> details;

    @NotEmpty(message = "Debe especificar los pagos")
    private List<@Valid PaymentRequestDTO> payments;

    @DecimalMin(value = "0.0", inclusive = true, message = "El recargo no puede ser negativo")
    private BigDecimal surcharge  = BigDecimal.ZERO;
}
