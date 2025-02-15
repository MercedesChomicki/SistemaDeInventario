package com.inventario.Inventario.exceptions;

public class SupplierNotFoundException extends RuntimeException {
    public SupplierNotFoundException(Integer id) {
        super("El proveedor con ID " + id + ", no existe.");
    }
}

