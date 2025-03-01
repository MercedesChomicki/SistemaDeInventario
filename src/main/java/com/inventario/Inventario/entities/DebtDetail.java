package com.inventario.Inventario.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "debt_details")
public class DebtDetail extends TransactionDetail{
    @ManyToOne
    @JoinColumn(name = "debt_id", nullable = false)
    private Debt debt;

    public DebtDetail() {
        super();
    }

    public DebtDetail(Debt debt, Product product, Integer quantity) {
        super(product, quantity, product.getCashPrice(), product.getCashPrice().multiply(BigDecimal.valueOf(quantity)));
        this.debt = debt;
    }
}