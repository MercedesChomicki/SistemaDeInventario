package com.inventario.Inventario.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor  // Crea un constructor vacío
@AllArgsConstructor // Crea un constructor con todos los atributos
@Getter
@Setter
@Entity
@Table(name = "customer_debts")
public class CustomerDebt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "debt_id")
    @Setter(AccessLevel.NONE)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime date;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "amount_paid", nullable = false)
    private double amountPaid;
}
