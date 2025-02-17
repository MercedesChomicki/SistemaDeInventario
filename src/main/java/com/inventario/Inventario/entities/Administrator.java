package com.inventario.Inventario.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "administrators")
public class Administrator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Integer id;

    @Column(nullable = false, length = 100)
    private String firstname;

    @Column(nullable = false, length = 100)
    private String lastname;

    @Column(nullable = false, length = 254, unique = true)
    private String email;

    @Column(nullable = false, length = 32)
    private String password;

    @Column(name = "phone", length = 45)
    private String mobileNumber;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime registration_time;

    // Constructor vacío requerido por JPA
    public Administrator() {}

    // Getters y Setters
    public Integer getId() {
        return id;
    }

}