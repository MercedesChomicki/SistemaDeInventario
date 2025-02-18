package com.inventario.Inventario.exceptions;

import java.io.Serializable;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Serializable id) {
        super(resourceName + " con ID " + id + " no existe.");
    }
}

