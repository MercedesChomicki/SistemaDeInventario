package com.inventario.Inventario.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "sale_details")
public class SaleDetail extends TransactionDetail {
    @ManyToOne
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    public SaleDetail() {
        super();
    }

    public SaleDetail(Sale sale, Product product, Integer quantity) {
        super(product, quantity, product.getCashPrice(), product.getCashPrice().multiply(BigDecimal.valueOf(quantity)));
        this.sale = sale;
    }
}