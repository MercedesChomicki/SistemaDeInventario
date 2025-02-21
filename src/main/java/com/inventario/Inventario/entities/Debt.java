package com.inventario.Inventario.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.inventario.Inventario.services.SaleService.CARD_SURCHARGE_PERCENTAGE;

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
    private List<Sale> sales = new ArrayList<>();

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

    @PrePersist
    public void prePersist() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).toLocalDateTime();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).toLocalDateTime();
    }

    // Método para aplicar recargo si no se paga en efectivo
    public void applySurchargeIfNeeded(boolean incash) {
        if (!incash) {
            BigDecimal surcharge = this.getAmountDue()
                    .multiply(CARD_SURCHARGE_PERCENTAGE)
                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            this.amountDue = this.amountDue.add(surcharge);
            this.amountTotal = this.amountTotal.add(surcharge);
        }
    }

    // Método para procesar un pago parcial
    public void processPartialPayment(BigDecimal amount) {
        this.amountPaid = this.amountPaid.add(amount);
        this.amountDue = this.amountDue.subtract(amount);

        BigDecimal remainingAmount = amount;
        for (Sale sale : this.sales) {
            BigDecimal amountDebt = sale.getTotal().subtract(sale.getPayInCash());
            if (remainingAmount.compareTo(amountDebt) >= 0) {
                sale.setStatus(SaleStatus.PAID);
                remainingAmount = remainingAmount.subtract(amountDebt);
            } else {
                break; // Si no se cubre la deuda total de una venta, se detiene
            }
        }
    }
}
