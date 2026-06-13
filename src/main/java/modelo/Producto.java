/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.math.BigDecimal;

/**
 *
 * @author natha
 */
public class Producto implements Comparable<Producto> {

    private int idProducto;
    private BigDecimal precio;
    private String descripcion;
    private String tipo;
    private int stock;

    public Producto() {
    }

    public Producto(int idProducto, BigDecimal precio, String descripcion,
            String tipo, int stock) {
        this.idProducto = idProducto;
        this.precio = precio;
        this.descripcion = descripcion;
        this.tipo = tipo;           
        this.stock = stock;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return descripcion;
    }

        @Override
    public int compareTo(Producto o) {
        Producto actual = this;
        return actual.getDescripcion().compareTo(o.getDescripcion());
    }
}
