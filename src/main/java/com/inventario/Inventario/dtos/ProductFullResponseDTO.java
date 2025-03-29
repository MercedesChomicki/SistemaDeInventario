package com.inventario.Inventario.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ProductFullResponseDTO {
    private int id;
    private String code;
    private String name;
    private String description;
    private BigDecimal cost;
    private BigDecimal salePrice;
    private Integer stock;
    private LocalDate expirationDate;
    private String species;
    private String category;
    private String supplier;
}
