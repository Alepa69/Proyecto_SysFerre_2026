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
public class Inventario {

    private int idInventario;
    private String nombreProducto;
    private BigDecimal precioUnitario;
    private String descripcion;
    private int stockDisponible;
    private int idProducto;
    private Producto producto; // objeto relacionado (opcional)

    public Inventario() {
    }

    public Inventario(int idInventario, String nombreProducto, BigDecimal precioUnitario,
            String descripcion, int stockDisponible, int idProducto) {
        this.idInventario = idInventario;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.descripcion = descripcion;
        this.stockDisponible = stockDisponible;
        this.idProducto = idProducto;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(int stockDisponible) {
        this.stockDisponible = stockDisponible;
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

    @Override
    public String toString() {
        return nombreProducto;
    }
}
