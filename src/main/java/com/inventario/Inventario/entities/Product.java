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
    @ToString.Include // Incluir en toString
    private Integer id;

    @Column(nullable = false, unique = true, length = 45)
    @ToString.Include // Incluir en toString
    private String code;

    @Column(nullable = false, length = 200)
    @ToString.Include // Incluir en toString
    private String name;

    private String description;

    @Column(name = "cash_price", nullable = false, precision = 10, scale = 2)
    @ToString.Include // Incluir en toString
    private BigDecimal cashPrice;

    @Column(nullable = false)
    @ToString.Include // Incluir en toString
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

    // Método para calcular el precio con tarjeta (si se necesita)
    public BigDecimal getPriceWithCard(BigDecimal cardPercentageIncrease) {
        return this.cashPrice.add(this.cashPrice.multiply(cardPercentageIncrease).divide(BigDecimal.valueOf(100)));
    }
}
