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

public class DetalleCompra implements Comparable<DetalleCompra> {

    private int idDetalle;
    private int cantidad;
    private BigDecimal precioUnitario;
    private int idCompra;
    private int idProducto;
    private Producto producto; // objeto relacionado (opcional)

    public DetalleCompra() {
    }

    public DetalleCompra(int idDetalle, int cantidad, BigDecimal precioUnitario,
            int idCompra, int idProducto) {
        this.idDetalle = idDetalle;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.idCompra = idCompra;
        this.idProducto = idProducto;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(new BigDecimal(cantidad));
    }

    @Override
    public String toString() {
        return "Detalle #" + idDetalle + " - Cant: " + cantidad;
    }

    @Override
    public int compareTo(DetalleCompra o) {
        DetalleCompra actual = this;
        return Integer.compare(actual.getIdDetalle(), o.getIdDetalle());
    }
}
