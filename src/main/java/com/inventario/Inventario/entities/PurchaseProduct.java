package com.inventario.Inventario.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "purchase_products")
public class PurchaseProduct {

    @EmbeddedId
    private PurchaseProductId id;

    @ManyToOne
    @MapsId("purchaseId")
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double unit_price;

    // Constructor vacío necesario para JPA
    public PurchaseProduct() {}

    // Constructor con parámetros
    public PurchaseProduct(Purchase purchase, Product product, int quantity, double unitPrice) {
        this.id = new PurchaseProductId(purchase.getId(), product.getId());
        this.purchase = purchase;
        this.product = product;
        this.quantity = quantity;
        this.unit_price = unitPrice;
    }

    // Getters y Setters
    public PurchaseProductId getId() {
        return id;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unit_price;
    }
}

