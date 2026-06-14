
package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 *
 * @author natha
 */
public class Venta implements Comparable<Venta> {

    private int idVenta;
    private LocalDate fecha;
    private LocalTime hora;
    private BigDecimal subtotal;
    private BigDecimal total;
    private int idEmpleado;
    private int idCliente;
    private Empleado empleado; // obj empl
    private Cliente cliente; // obj clientw
    private List<DetalleVenta> detalles; // lista de detalles ventas

    public Venta() {
    }

    public Venta(int idVenta, LocalDate fecha, LocalTime hora,
            BigDecimal subtotal, BigDecimal total,
            int idEmpleado, int idCliente) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.hora = hora;
        this.subtotal = subtotal;
        this.total = total;
        this.idEmpleado = idEmpleado;
        this.idCliente = idCliente;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        return "Venta #" + idVenta + " - " + fecha;
    }

    @Override
    public int compareTo(Venta o) {
        // Ordenar por fecha, luego por ID si la fecha es igual
        int cmp = this.fecha.compareTo(o.getFecha());
        if (cmp != 0)
            return cmp;
        return Integer.compare(this.idVenta, o.getIdVenta());
    }

}
