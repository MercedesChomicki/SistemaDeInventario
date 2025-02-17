package com.inventario.Inventario.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Integer id) {
        super(resourceName + " con ID " + id + " no existe.");
    }
}

