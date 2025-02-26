package com.inventario.Inventario.dtos;

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
    private LocalDateTime date;
    private List<SaleDetailResponseDTO> details;
    private List<SalePaymentResponseDTO> payments;
}
