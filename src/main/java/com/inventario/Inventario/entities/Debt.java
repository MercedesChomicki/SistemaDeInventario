package com.inventario.Inventario.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor  // Crea un constructor vacío
@AllArgsConstructor // Crea un constructor con todos los atributos
@Getter
@Setter
@Entity
@Table(name = "debts")
public class Debt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "debt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DebtDetail> details = new ArrayList<>();

    @Column(name = "amount_total", nullable = false)
    private BigDecimal amountTotal = BigDecimal.ZERO;

    @Column(name = "amount_due", nullable = false)
    private BigDecimal amountDue = BigDecimal.ZERO;

    @Column(name = "amount_paid", nullable = false)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "surcharge", nullable = false)
    private BigDecimal surcharge = BigDecimal.ZERO;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DebtStatus status = DebtStatus.PENDING;

    public Debt(Customer customer, LocalDateTime createdAt) {
        this.customer = customer;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).toLocalDateTime();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).toLocalDateTime();
    }

    public void recalculateDebt(BigDecimal total, BigDecimal amount, BigDecimal surcharge) {
        this.surcharge = this.surcharge.add(surcharge);
        this.amountTotal = total != null ? total.add(surcharge) : amountTotal.add(surcharge);
        this.amountPaid = this.amountPaid.add(amount);
        this.amountDue = amountTotal.subtract(this.amountPaid);
        this.status = (this.amountDue.compareTo(BigDecimal.ZERO) == 0) ? DebtStatus.PAID
                    : (this.amountDue.compareTo(amountTotal) == 0) ? DebtStatus.PENDING
                    : DebtStatus.PARTIALLY_PAID;
        this.updatedAt = ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).toLocalDateTime();
    }
}
