package com.inventario.Inventario.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartProductResponseDTO {
    private Integer productId;
    private String productName;
    private int quantity;
    private double price;
}
