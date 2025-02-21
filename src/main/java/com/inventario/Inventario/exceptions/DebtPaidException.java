package com.inventario.Inventario.exceptions;

public class DebtPaidException extends RuntimeException {
    public DebtPaidException(String message) {
        super(message);
    }
}