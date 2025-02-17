package com.inventario.Inventario.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor  // Crea un constructor vacío
@AllArgsConstructor // Crea un constructor con todos los atributos
@Getter
@EqualsAndHashCode
@Embeddable
public class PurchaseProductId implements Serializable {

    private Integer purchaseId;
    private Integer productId;
}

