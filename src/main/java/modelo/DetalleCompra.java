/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author alexi
 */
public class DetalleCompra implements Comparable<DetalleCompra> {
    private int idProveedor;
    private Proveedor proveedor;
    private int idDetalle;
    private int cantidad;
    private double precioUnitario;
    private int idCompra;
    private int idProducto;
    private Producto producto;

    public DetalleCompra() {
    }

    public DetalleCompra(int idProveedor, Proveedor proveedor, int idDetalle, int cantidad, double precioUnitario, int idCompra, int idProducto, Producto producto) {
        this.idProveedor = idProveedor;
        this.proveedor = proveedor;
        this.idDetalle = idDetalle;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.idCompra = idCompra;
        this.idProducto = idProducto;
        this.producto = producto;
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

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
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

    public double getSubtotal() {
        return this.precioUnitario * this.cantidad;
    }

    @Override
    public int compareTo(DetalleCompra otro) {
        // Compara los detalles de compra basándose en su ID único para el Árbol B
        return Integer.compare(this.idDetalle, otro.idDetalle);
    }

    @Override
    public String toString() {
        return "Detalle #" + idDetalle + " - Cant: " + cantidad;
    }
}
