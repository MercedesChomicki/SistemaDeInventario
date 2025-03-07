package com.inventario.Inventario.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor  // Crea un constructor vacío
@AllArgsConstructor // Crea un constructor con todos los atributos
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

    @Column(name = "cash_price", nullable = false, precision = 10, scale = 2)
    @ToString.Include
    private BigDecimal cashPrice;

    @Column(name = "purchase_price", nullable = false, precision = 10, scale = 2)
    @ToString.Include
    private BigDecimal purchasePrice;

    @Column(name = "percentage_increase", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentageIncrease;

    @Column(nullable = false)
    @ToString.Include
    private int stock;

    @Column(name = "image_url", length = 254)
    private String imageUrl;

    @Column(nullable = false)
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

    public Product (String code, String name, String description, BigDecimal purchasePrice,
                    BigDecimal percentageIncrease, BigDecimal cashPrice, int stock, String imageUrl,
                    LocalDate expirationDate, Species species, Category category, Supplier supplier
    ) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.purchasePrice = purchasePrice;
        this.percentageIncrease = percentageIncrease;
        this.cashPrice = cashPrice.compareTo(BigDecimal.ZERO) > 0 ? cashPrice : purchasePrice.add(purchasePrice.multiply(percentageIncrease.divide(new BigDecimal("100"))));
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.expirationDate = expirationDate;
        this.species = species;
        this.category = category;
        this.supplier = supplier;
    }

    private void updateCashPrice() {
        this.cashPrice = this.purchasePrice.add(this.purchasePrice.multiply(this.percentageIncrease.divide(new BigDecimal("100"))));
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
        updateCashPrice();
    }

    public void setPercentageIncrease(BigDecimal percentageIncrease) {
        this.percentageIncrease = percentageIncrease;
        updateCashPrice();
    }

}
