package com.inventario.Inventario.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor  // Crea un constructor vacío
@AllArgsConstructor // Crea un constructor con todos los atributos
@Getter
@Setter
@Entity
@Table(name = "debt_details")
public class DebtDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "debt_id", nullable = false)
    private Debt debt;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private BigDecimal subtotal;

    public DebtDetail(Debt debt, Product product, Integer quantity) {
        this.debt = debt;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getCashPrice();
        this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void updateDetailValues(Integer quantity){
        this.unitPrice = this.product.getCashPrice();
        this.quantity += quantity;
        this.subtotal = this.subtotal.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));
    }
}