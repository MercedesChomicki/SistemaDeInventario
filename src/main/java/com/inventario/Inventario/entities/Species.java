package com.inventario.Inventario.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "species")
public class Species {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "species_id")
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public Species() {}

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

