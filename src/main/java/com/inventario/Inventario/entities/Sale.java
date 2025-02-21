package com.inventario.Inventario.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor  // Crea un constructor vacío
@AllArgsConstructor // Crea un constructor con todos los atributos
@Getter
@Setter
@Entity
@Table(name = "sales")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod = PaymentMethod.CASH;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus status = SaleStatus.PAID;

    @Column(nullable = false, updatable = false)
    private LocalDateTime date;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleDetail> details;

    @ManyToOne
    @JoinColumn(name = "debt_id")
    private Debt debt;

    // Para el caso en el que quiera pagar mixto
    @Column(name = "paid_in_cash")
    private BigDecimal payInCash;

    @Column(name = "paid_by_card")
    private BigDecimal payByCard = BigDecimal.ZERO;

    @PrePersist
    public void prePersist() {
        this.date = ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).toLocalDateTime();
    }
}