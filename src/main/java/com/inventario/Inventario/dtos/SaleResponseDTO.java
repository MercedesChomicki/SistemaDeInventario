package com.inventario.Inventario.dtos;

import com.inventario.Inventario.entities.PaymentMethod;
import com.inventario.Inventario.entities.SaleStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SaleResponseDTO {
    private Long id;
    private BigDecimal total;
    private PaymentMethod paymentMethod;
    private SaleStatus status;
    private LocalDateTime date;
    private List<SaleDetailResponseDTO> details;
    private Integer debtId;
    private BigDecimal paidInCash;
    private BigDecimal paidByCard;
}
