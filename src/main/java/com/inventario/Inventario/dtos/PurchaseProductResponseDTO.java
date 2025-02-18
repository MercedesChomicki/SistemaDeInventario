package com.inventario.Inventario.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PurchaseProductResponseDTO {
    private Integer purchaseId;
    private Integer productId;
    private int quantity;
    private double unitPrice;
}
