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
public class Inventario implements Comparable<Inventario> {

    private int idInventario;
    private String nombreProducto;
    private BigDecimal precioUnitario;
    private String descripcion;
    private int stockDisponible;
    private int idProducto;
    private Producto producto; // objeto relacionado (opcional)
    private String criterioOrden = "ID"; // criterio usado al ordenar/buscar en el ArbolBusqueda

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

    public String getCriterioOrden() {
        return criterioOrden;
    }

    public void setCriterioOrden(String criterioOrden) {
        this.criterioOrden = (criterioOrden == null || criterioOrden.isEmpty()) ? "ID" : criterioOrden;
    }

    @Override
    public String toString() {
        return nombreProducto;
    }

    @Override
    public int compareTo(Inventario o) {
        int c;
        switch (criterioOrden == null ? "ID" : criterioOrden) {
            case "Nombre A-Z":
                c = compararTexto(this.nombreProducto, o.nombreProducto);
                break;
            case "Precio":
                c = compararPrecio(this.precioUnitario, o.precioUnitario);
                break;
            case "Stock Ascendente":
            case "Stock Descendente":
                c = Integer.compare(this.stockDisponible, o.stockDisponible);
                break;
            case "ID":
            default:
                c = Integer.compare(this.idInventario, o.idInventario);
        }
        // Desempate estable por ID para no perder registros con valores repetidos
        if (c == 0) {
            c = Integer.compare(this.idInventario, o.idInventario);
        }
        return c;
    }

    private int compararTexto(String a, String b) {
        return (a == null ? "" : a).compareToIgnoreCase(b == null ? "" : b);
    }

    private int compararPrecio(BigDecimal a, BigDecimal b) {
        return (a == null ? BigDecimal.ZERO : a).compareTo(b == null ? BigDecimal.ZERO : b);
    }
}
