package com.inventario.Inventario.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor  // Crea un constructor vacío
@AllArgsConstructor // Crea un constructor con todos los atributos
@Getter
@Entity
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private Integer id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime date;
}


