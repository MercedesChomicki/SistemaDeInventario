package com.inventario.Inventario.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_products")
public class CartProduct {

    @EmbeddedId
    private CartProductId id;

    @ManyToOne
    @MapsId("cartId")
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double price;

    // Constructor vacío necesario para JPA
    public CartProduct() {}

    public CartProduct(Cart cart, Product product, int quantity, double price) {
        this.id = new CartProductId(cart.getId(), product.getId());
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters y Setters
    public CartProductId getId() {
        return id;
    }

}

