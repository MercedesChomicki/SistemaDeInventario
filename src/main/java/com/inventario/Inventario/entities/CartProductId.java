package com.inventario.Inventario.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

import lombok.NoArgsConstructor;

@NoArgsConstructor  // Crea un constructor vacío
@AllArgsConstructor // Crea un constructor con todos los atributos
@Getter
@EqualsAndHashCode
@Embeddable
public class CartProductId implements Serializable {

    private Integer cartId;
    private Integer productId;
}

