package com.inventario.Inventario.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)  // Solo incluye los campos especificados
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    @Setter(AccessLevel.NONE)
    @ToString.Include
    private Integer id;

    @Column(nullable = false, unique = true, length = 45)
    @ToString.Include
    private String code;

    @Column(nullable = false, length = 200)
    @ToString.Include
    private String name;

    private String description;

    @Column(name = "cost", nullable = false, precision = 10, scale = 2)
    @ToString.Include
    private BigDecimal cost;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @ToString.Include
    private BigDecimal price;

    @Column(name = "percentage_increase", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentageIncrease;

    @Column(nullable = false)
    @ToString.Include
    private int stock;

    @Column(name = "image_url", length = 254)
    private String imageUrl;

    private boolean active = true;
    private LocalDate expirationDate;

    @ManyToOne
    @JoinColumn(name = "species_id", nullable = false)
    private Species species;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    public Product (String code, String name, String description, BigDecimal cost,
                    BigDecimal percentageIncrease, BigDecimal price, int stock, String imageUrl,
                    LocalDate expirationDate, Species species, Category category, Supplier supplier
    ) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.percentageIncrease = percentageIncrease;
        this.price = price.compareTo(BigDecimal.ZERO) > 0 ? price : cost.add(cost.multiply(percentageIncrease.divide(new BigDecimal("100"), RoundingMode.HALF_UP)));
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.expirationDate = expirationDate;
        this.species = species;
        this.category = category;
        this.supplier = supplier;
    }

    private void updateCashPrice() {
        this.price = this.cost.add(this.cost.multiply(this.percentageIncrease.divide(new BigDecimal("100"), RoundingMode.HALF_UP)));
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.cost = purchasePrice;
        updateCashPrice();
    }

    public void setPercentageIncrease(BigDecimal percentageIncrease) {
        this.percentageIncrease = percentageIncrease;
        updateCashPrice();
    }
}
