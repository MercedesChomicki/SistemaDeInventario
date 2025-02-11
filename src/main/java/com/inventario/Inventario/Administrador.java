package com.inventario.Inventario;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "administradores")
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_admin")
    private Integer idAdmin;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(name = "email", nullable = false, length = 254, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 32)
    private String password;

    @Column(name = "nro_celular", length = 45)
    private String nroCelular;

    @Column(name = "hora_registracion", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime horaRegistracion;

    // Constructor vacío requerido por JPA
    public Administrador() {}

    // Getters y Setters
    public Integer getIdAdmin() {
        return idAdmin;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNroCelular() {
        return nroCelular;
    }

    public void setNroCelular(String nroCelular) {
        this.nroCelular = nroCelular;
    }

    public LocalDateTime getHoraRegistracion() {
        return horaRegistracion;
    }

    public void setHoraRegistracion(LocalDateTime horaRegistracion) {
        this.horaRegistracion = horaRegistracion;
    }
}