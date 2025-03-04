package com.inventario.Inventario.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
    private Integer cartId;
    private Integer userId;
    private List<DetailResponseDTO> details;
    private BigDecimal total;
}
