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
@Table(name = "sale_details")
public class SaleDetail extends TransactionDetail {
    @ManyToOne
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    public SaleDetail(Sale sale, Product product, Integer quantity) {
        super(product, quantity, product.getCashPrice());
        this.sale = sale;
    }
}