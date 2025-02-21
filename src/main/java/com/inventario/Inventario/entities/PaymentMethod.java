package com.inventario.Inventario.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
    CASH, CARD, MIXED, CREDIT;

    /**
     * ¿Qué hace esto?
     * @JsonCreator → Permite convertir correctamente el String recibido en un enum, ignorando mayúsculas/minúsculas.
     * @JsonValue → Define cómo se serializa el enum cuando se convierte a JSON.
     * Si el usuario envía "cArd", "Card", "card", o "CASH", el código aceptará el valor correcto.
       Pero si por ejemplo envía "CASRD", lanzará un error con un mensaje claro:
       --> Método de pago inválido: CASRD
     */
    @JsonCreator
    public static PaymentMethod fromString(String value) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.name().equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Método de pago inválido: " + value);
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}
