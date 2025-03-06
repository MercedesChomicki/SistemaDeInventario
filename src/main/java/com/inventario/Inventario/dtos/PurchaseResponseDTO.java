package com.inventario.Inventario.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PurchaseResponseDTO {
    private Long id;
    private Integer supplierId;
    private List<DetailResponseDTO> details;
    private BigDecimal total;
    private List<PaymentResponseDTO> payments;
    private BigDecimal surcharge;
    private LocalDateTime date;
}
