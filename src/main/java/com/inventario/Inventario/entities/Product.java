package com.inventario.Inventario.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
    private BigDecimal salePrice;

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

    public Product (String code, String name, String description, BigDecimal cost, BigDecimal salePrice, int stock,
                    String imageUrl, LocalDate expirationDate, Species species, Category category, Supplier supplier
    ) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.salePrice = salePrice;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.expirationDate = expirationDate;
        this.species = species;
        this.category = category;
        this.supplier = supplier;
    }
}
