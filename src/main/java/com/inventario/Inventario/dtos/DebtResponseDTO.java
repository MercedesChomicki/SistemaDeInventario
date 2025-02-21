package com.inventario.Inventario.dtos;

import com.inventario.Inventario.entities.Sale;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DebtResponseDTO {
    private Integer id;
    private Integer customerId;
    private BigDecimal amountTotal;
    private BigDecimal amountDue;
    private BigDecimal amountPaid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
