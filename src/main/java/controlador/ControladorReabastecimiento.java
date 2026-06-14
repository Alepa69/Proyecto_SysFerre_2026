/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import dao.CompraDAO;
import dao.ProductoDAO;
import modelo.CompraProveedor;
import modelo.DetalleCompra;
import modelo.Producto;
import modelo.Proveedor;
import com.ues.group.vista.VistaReabastecimiento;
import com.ues.group.vista.VistaReabastecimientoHistorial;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.Window;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author alexi
 */
public class ControladorReabastecimiento {
    double total = 0;
    private VistaReabastecimiento vista;
    private CompraDAO dao;
    private ProductoDAO productoDAO;

    public ControladorReabastecimiento(VistaReabastecimiento vista) {
        this.vista = vista;
        this.dao = new CompraDAO();
        this.productoDAO = new ProductoDAO();

        cargarCombos();
        vista.getBtnAgregar().addActionListener(e -> agregarDetalle());
        vista.getBtnEfectuar().addActionListener(e -> registrar());
        vista.getBtnHistorial().addActionListener(e -> historial());
        vista.getBtnLimpiar().addActionListener(e -> limpiar());
    }

    private void historial() {
        VistaReabastecimientoHistorial vistaHistorial = new VistaReabastecimientoHistorial();
        ControladorHistorialReabastecimiento control = new ControladorHistorialReabastecimiento(vistaHistorial);
        vistaHistorial.setVisible(true);
    }

    private void cargarCombos() {
        try {
            vista.getCmbProveedor().removeAllItems();
            vista.getCmbProducto().removeAllItems();

            for (Proveedor p: dao.listarProveedores()) {
                vista.getCmbProveedor().addItem(p);
            }

            for (Producto p: productoDAO.listar().IND()) {
                vista.getCmbProducto().addItem(p);
            }
        } catch (Exception e) {
            mostrarMensaje("Error al cargar los cmb: " + e.getMessage());
        }
    }

    private void agregarDetalle() {
        if (!validarCamposDetalle()) {
            return;
        }

        try {
            Producto producto = (Producto) vista.getCmbProducto().getSelectedItem();
            Proveedor proveedor = (Proveedor) vista.getCmbProveedor().getSelectedItem();

            int cantidad = Integer.parseInt(vista.getTxtCantidad().getText().trim());
            double precio = producto.getPrecio().doubleValue();
            double subtotal = cantidad * precio;

            getModeloTabla().addRow(new Object[] {
                    producto.getIdProducto(),
                    producto.getDescripcion(),
                    cantidad,
                    precio,
                    proveedor,
                    subtotal
            });

            actualizarTotal();
        } catch (Exception e) {
            mostrarMensaje("No se pudo agregar al carrito: " + e.getMessage());
        }
    }

    private void actualizarTotal() {

        DefaultTableModel modelo = getModeloTabla();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            total += Double.parseDouble(modelo.getValueAt(i, 5).toString());
        }
        vista.getTxtTotal().setText(String.valueOf(total));
        vista.getTxtCantidad().setText("");
    }

    private void registrar() {
        DefaultTableModel modelo = getModeloTabla();

        if (modelo.getRowCount() == 0) {
            mostrarMensaje("Agregue al menos un producto al carrito");
            return;
        }

        /*
         * String totalStr = vista.getTxtTotal().getText().trim();
         * if (totalStr.isEmpty()) {
         * return;
         * }
         */

        try {
            CompraProveedor compra = new CompraProveedor();
            compra.setFecha(LocalDate.now());
            compra.setCategoria("123");
            compra.setTotalCompra(total);
            compra.setDetalles(new ArrayList<>());

            for (int i = 0; i < modelo.getRowCount(); i++) {
                DetalleCompra detalle = new DetalleCompra();
                detalle.setIdProducto(Integer.parseInt(modelo.getValueAt(i, 0).toString()));
                detalle.setCantidad(Integer.parseInt(modelo.getValueAt(i, 2).toString()));
                detalle.setPrecioUnitario(Double.parseDouble(modelo.getValueAt(i, 3).toString()));

                Proveedor prov = (Proveedor) modelo.getValueAt(i, 4);
                detalle.setIdProveedor(prov.getIdProveedor());
                compra.getDetalles().add(detalle);
            }

            dao.registrar(compra);
            mostrarMensaje("datos de pedido guardados");
            limpiar();

        } catch (Exception e) {
            mostrarMensaje("error al registrar:" + e.getMessage());
        }
    }

    private void limpiar() {
        vista.getTxtCantidad().setText("");
        vista.getTxtTotal().setText("");
        vista.getCmbProveedor().setSelectedIndex(0);
        if (vista.getCmbProducto().getItemCount() > 0) {
            vista.getCmbProducto().setSelectedIndex(0);
        }
        getModeloTabla().setRowCount(0);
    }

    private boolean validarCamposDetalle() {
        String cantStr = vista.getTxtCantidad().getText().trim();

        if (cantStr.isEmpty()) {
            mostrarMensaje("Ingrese la cantidad a comprar");
            return false;
        }

        try {
            if (Integer.parseInt(cantStr) <= 0) {
                mostrarMensaje("La cantidad debe ser mayor a 0");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            mostrarMensaje("Ingrese numeros");
            return false;
        }
    }

    private DefaultTableModel getModeloTabla() {
        return (DefaultTableModel) vista.getTblDetalleCompra().getModel();
    }

    private void mostrarMensaje(String mensaje) {
        Window ventanaPadre = SwingUtilities.getWindowAncestor(vista);
        JOptionPane.showMessageDialog(ventanaPadre != null ? ventanaPadre : vista, mensaje);
    }
}