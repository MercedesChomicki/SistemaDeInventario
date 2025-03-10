package com.inventario.Inventario.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "purchase_details")
public class PurchaseDetail extends TransactionDetail {
    @ManyToOne
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    public PurchaseDetail(Purchase purchase, Product product, Integer quantity) {
        super(product, quantity, product.getCost());
        this.purchase = purchase;
    }
}