package com.inventario.Inventario.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PurchaseProductId implements Serializable {

    private Integer purchaseId;
    private Integer productId;

    public PurchaseProductId() {}

    public PurchaseProductId(Integer purchaseId, Integer productId) {
        this.purchaseId = purchaseId;
        this.productId = productId;
    }

    // Getters y Setters
    public Integer getPurchaseId() {
        return purchaseId;
    }

    public Integer getProductId() {
        return productId;
    }

    // Métodos equals() y hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseProductId that = (PurchaseProductId) o;
        return Objects.equals(purchaseId, that.purchaseId) && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseId, productId);
    }
}

