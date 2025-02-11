package com.inventario.Inventario;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CompraProductoId implements Serializable {

    private Integer idCompra;
    private Integer idProducto;

    public CompraProductoId() {}

    public CompraProductoId(Integer idCompra, Integer idProducto) {
        this.idCompra = idCompra;
        this.idProducto = idProducto;
    }

    // Getters y Setters
    public Integer getIdCompra() {
        return idCompra;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    // Métodos equals() y hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompraProductoId that = (CompraProductoId) o;
        return Objects.equals(idCompra, that.idCompra) && Objects.equals(idProducto, that.idProducto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCompra, idProducto);
    }
}

