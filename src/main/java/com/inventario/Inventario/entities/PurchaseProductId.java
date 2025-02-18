package com.inventario.Inventario.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor  // Crea un constructor vacío
@AllArgsConstructor // Crea un constructor con todos los atributos
@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class PurchaseProductId implements Serializable {

    private Integer purchaseId;
    private Integer productId;
}

