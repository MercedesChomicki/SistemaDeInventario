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
    private List<DebtDetail> details;

    @Column(name = "amount_total", nullable = false)
    private BigDecimal amountTotal;

    @Column(name = "amount_due", nullable = false)
    private BigDecimal amountDue;

    @Column(name = "amount_paid", nullable = false)
    private BigDecimal amountPaid;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DebtStatus status;

    public Debt(Customer customer, LocalDateTime createdAt) {
        this.customer = customer;
        this.createdAt = createdAt;
        this.amountTotal = BigDecimal.ZERO;
        this.amountDue = BigDecimal.ZERO;
        this.amountPaid = BigDecimal.ZERO;
        this.status = DebtStatus.PENDING;
        this.details = new ArrayList<>();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).toLocalDateTime();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).toLocalDateTime();
    }

    public void updateDebtValues(BigDecimal total, BigDecimal amountPaid) {
        this.amountTotal = total;
        this.amountPaid = this.amountPaid.add(amountPaid);
        this.amountDue = amountTotal.subtract(this.amountPaid);
        this.status = amountPaid.compareTo(BigDecimal.ZERO) > 0 ? DebtStatus.PARTIALLY_PAID : DebtStatus.PENDING;
        this.updatedAt = ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).toLocalDateTime();
    }


    // Método para aplicar recargo si no se paga en efectivo
    /*public void applySurchargeIfNeeded(boolean incash) {
        if (!incash) {
            BigDecimal surcharge = this.getAmountDue()
                    .multiply(SURCHARGE_PERCENTAGE)
                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            this.amountDue = this.amountDue.add(surcharge);
            this.amountTotal = this.amountTotal.add(surcharge);
        }
    }*/
}
