package com.inventario.Inventario.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Integer id) {
        super("El producto con el ID " + id + ", no existe.");
    }
}

