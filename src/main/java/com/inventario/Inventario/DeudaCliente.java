package com.inventario.Inventario;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "deudas_clientes")
public class DeudaCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_deuda")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "fecha", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fecha;

    @Column(nullable = false, name = "monto_total")
    private double montoTotal;

    @Column(nullable = false, name = "monto_pagado")
    private double montoPagado;
}
