package com.inventario.Inventario.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "cart_details")
public class CartDetail extends TransactionDetail {
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    public CartDetail() {
        super();
    }

    public CartDetail(Cart cart, Product product, Integer quantity) {
        super(product, quantity, product.getCashPrice(), product.getCashPrice().multiply(BigDecimal.valueOf(quantity)));
        this.cart = cart;
    }
}