package com.inventario.Inventario;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductoCarritoId implements Serializable {

    private Integer idCarrito;
    private Integer idProducto;

    public ProductoCarritoId() {}

    public ProductoCarritoId(Integer idCarrito, Integer idProducto) {
        this.idCarrito = idCarrito;
        this.idProducto = idProducto;
    }

    // Getters y Setters
    public Integer getIdCarrito() {
        return idCarrito;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    // Métodos equals() y hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductoCarritoId that = (ProductoCarritoId) o;
        return Objects.equals(idCarrito, that.idCarrito) && Objects.equals(idProducto, that.idProducto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCarrito, idProducto);
    }
}

