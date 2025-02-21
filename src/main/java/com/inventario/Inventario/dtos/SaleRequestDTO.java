package com.inventario.Inventario.dtos;

import com.inventario.Inventario.entities.PaymentMethod;
import com.inventario.Inventario.entities.SaleStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class SaleRequestDTO {

    @NotNull
    private PaymentMethod paymentMethod;
    @NotNull
    private SaleStatus status;
    @NotNull
    private LocalDateTime date;
    @NotEmpty
    private List<SaleDetailRequestDTO> details;

    // Para el caso de crear una deuda
    private Integer customerId;

    // Para el caso de pagar mixto
    private BigDecimal paidInCash;
    private BigDecimal paidByCard;
}
