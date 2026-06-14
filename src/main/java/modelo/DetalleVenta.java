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

public class DetalleVenta {

    private int idDetalle;
    private int cantidad;
    private BigDecimal precioUnitario;
    private int idVenta;
    private int idProducto;
    private Producto producto; // objeto relacionado (opcional)

    public DetalleVenta() {
    }

    public DetalleVenta(int idDetalle, int cantidad, BigDecimal precioUnitario,
            int idVenta, int idProducto) {
        this.idDetalle = idDetalle;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.idVenta = idVenta;
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

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
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
}
