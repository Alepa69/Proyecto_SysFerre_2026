/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import dao.CompraDAO;
import modelo.CompraProveedor;
import modelo.DetalleCompra;
import com.ues.group.vista.VistaReabastecimientoHistorial;

import Arboles.ArbolBusqueda;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author alexi
 */
public class ControladorHistorialReabastecimiento  {
    private VistaReabastecimientoHistorial vista;
    private CompraDAO dao;
    private List<CompraProveedor> listaCompras; 
    public ControladorHistorialReabastecimiento(VistaReabastecimientoHistorial vista) {
        this.vista = vista;
        this.dao = new CompraDAO();
        
        vista.getBtnMostrar().addActionListener(e ->mostrarDetalles());
        cargarTablaHistorial(); 
    }

    private void cargarTablaHistorial() {
        DefaultTableModel modeloHistorial = (DefaultTableModel) vista.getTblHistorial().getModel();
        modeloHistorial.setRowCount(0); 
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            listaCompras = dao.listarHistorialCompras();
            
            for (CompraProveedor c : listaCompras) {
                String fechaFormateada = "";
                if (c.getFecha() != null) {
                    fechaFormateada = c.getFecha().format(formateador);
                }
                                modeloHistorial.addRow(new Object[]{
                    c.getIdCompra(), 
                    fechaFormateada, 
                    c.getTotalCompra()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar el historial: " + e.getMessage());
        }
    }

    private void mostrarDetalles() {
        int filaSeleccionada = vista.getTblHistorial().getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "no seleccionaste nada"); return;
        }
        CompraProveedor compraSeleccionada = listaCompras.get(filaSeleccionada);
        DefaultTableModel modeloDetalle = (DefaultTableModel) vista.getTblDetalleCompra().getModel();
        modeloDetalle.setRowCount(0); 
        try {
            ArbolBusqueda<DetalleCompra> arbol = dao.buscarDetalles(compraSeleccionada.getIdCompra());

            for (DetalleCompra d : arbol.IND()) {
                String nombreProducto = "Sin descripción";
                if (d.getProducto() != null) {
                    nombreProducto = d.getProducto().getDescripcion();
                }
                //String nombreProveedor = "dasdadsdasfda";
                String nombreProveedor=null;
                if (d.getProveedor() != null) {
                    nombreProveedor = d.getProveedor().getNombre();
                }
                modeloDetalle.addRow(new Object[]{
                    d.getIdProducto(),
                    nombreProducto,
                    d.getCantidad(),
                    d.getPrecioUnitario(),
                    nombreProveedor, 
                    d.getSubtotal()
                });
            }
            
            
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar los detalles: " + e.getMessage());
        }
    }
}