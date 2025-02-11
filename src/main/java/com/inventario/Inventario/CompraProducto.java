package com.inventario.Inventario;

import jakarta.persistence.*;

@Entity
@Table(name = "compra_productos")
public class CompraProducto {

    @EmbeddedId
    private CompraProductoId id;

    @ManyToOne
    @MapsId("idCompra")
    @JoinColumn(name = "id_compra", nullable = false)
    private Compra compra;

    @ManyToOne
    @MapsId("idProducto")
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private double precioUnitario;

    // Constructor vacío necesario para JPA
    public CompraProducto() {}

    // Constructor con parámetros
    public CompraProducto(Compra compra, Producto producto, int cantidad, double precioUnitario) {
        this.id = new CompraProductoId(compra.getIdCompra(), producto.getIdProducto());
        this.compra = compra;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // Getters y Setters
    public CompraProductoId getId() {
        return id;
    }

    public void setId(CompraProductoId id) {
        this.id = id;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}

