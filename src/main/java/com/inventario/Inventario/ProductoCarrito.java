package com.inventario.Inventario;

import jakarta.persistence.*;

@Entity
@Table(name = "productos_carrito")
public class ProductoCarrito {

    @EmbeddedId
    private ProductoCarritoId id;

    @ManyToOne
    @MapsId("idCarrito")
    @JoinColumn(name = "id_carrito", nullable = false)
    private Carrito carrito;

    @ManyToOne
    @MapsId("idProducto")
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Column(name = "precio", nullable = false)
    private double precio;

    // Constructor vacío necesario para JPA
    public ProductoCarrito() {}

    // Constructor con parámetros
    public ProductoCarrito(Carrito carrito, Producto producto, int cantidad, double precio) {
        this.id = new ProductoCarritoId(carrito.getIdCarrito(), producto.getIdProducto());
        this.carrito = carrito;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    // Getters y Setters
    public ProductoCarritoId getId() {
        return id;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}

