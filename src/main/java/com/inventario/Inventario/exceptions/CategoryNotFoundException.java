package com.inventario.Inventario.exceptions;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Integer id) {
        super("La categoría con el ID " + id + ", no existe.");
    }
}