package com.inventario.Inventario.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "debt_details")
public class DebtDetail extends TransactionDetail{
    @ManyToOne
    @JoinColumn(name = "debt_id", nullable = false)
    private Debt debt;

    public DebtDetail(Debt debt, Product product, Integer quantity) {
        super(product, quantity, product.getPrice());
        this.debt = debt;
    }
}