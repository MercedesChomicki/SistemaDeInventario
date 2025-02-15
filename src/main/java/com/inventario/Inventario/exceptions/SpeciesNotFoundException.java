package com.inventario.Inventario.exceptions;

public class SpeciesNotFoundException extends RuntimeException {
    public SpeciesNotFoundException(Integer id) {
        super("La especie con ID " + id + ", no existe.");
    }
}

