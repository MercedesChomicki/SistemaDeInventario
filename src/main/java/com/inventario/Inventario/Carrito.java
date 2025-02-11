package com.inventario.Inventario;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "carritos")
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Integer idCarrito;

    @Column(name = "fecha", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fecha_creacion;

    // Getters y Setters
    public Integer getIdCarrito() {
        return idCarrito;
    }
}