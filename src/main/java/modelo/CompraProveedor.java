/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author alexi
 */
public class CompraProveedor implements Comparable<CompraProveedor> {

    private int idCompra;
    private String categoria;
    private LocalDate fecha;
    private Double totalProveedor;
    private Double totalCompra;
    private int idProveedor;
    private Proveedor proveedor;
    private List<DetalleCompra> detalles;

    public CompraProveedor() {
    }

    public CompraProveedor(int idCompra, String categoria, LocalDate fecha, Double totalProveedor, Double totalCompra, int idProveedor) {
        this.idCompra = idCompra;
        this.categoria = categoria;
        this.fecha = fecha;
        this.totalProveedor = totalProveedor;
        this.totalCompra = totalCompra;
        this.idProveedor = idProveedor;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Double getTotalProveedor() {
        return totalProveedor;
    }

    public void setTotalProveedor(Double totalProveedor) {
        this.totalProveedor = totalProveedor;
    }

    public Double getTotalCompra() {
        return totalCompra;
    }

    public void setTotalCompra(Double totalCompra) {
        this.totalCompra = totalCompra;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public List<DetalleCompra> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCompra> detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        return "Compra #" + idCompra + " - " + fecha;
    }

    @Override
    public int compareTo(CompraProveedor o) {
        return this.fecha.compareTo(o.getFecha());
    }
}
