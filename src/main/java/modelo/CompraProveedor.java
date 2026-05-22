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
 * @author natha
 */
public class CompraProveedor {

    private int idCompra;
    private String categoria;
    private LocalDate fecha;
    private BigDecimal totalProveedor;
    private BigDecimal totalCompra;
    private int idProveedor;
    private Proveedor proveedor;         // objeto relacionado (opcional)
    private List<DetalleCompra> detalles; // lista de detalles (opcional)

    public CompraProveedor() {
    }

    public CompraProveedor(int idCompra, String categoria, LocalDate fecha,
            BigDecimal totalProveedor, BigDecimal totalCompra, int idProveedor) {
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

    public BigDecimal getTotalProveedor() {
        return totalProveedor;
    }

    public void setTotalProveedor(BigDecimal totalProveedor) {
        this.totalProveedor = totalProveedor;
    }

    public BigDecimal getTotalCompra() {
        return totalCompra;
    }

    public void setTotalCompra(BigDecimal totalCompra) {
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
}
