package com.inventario.Inventario.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductResponseDTO {
    private int id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String species;
    private String category;
    private String supplier;
}
