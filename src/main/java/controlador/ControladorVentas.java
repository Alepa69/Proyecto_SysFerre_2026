/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.ues.group.vista.VistaVentas;
import dao.ClienteDAO;
import dao.InventarioDAO;
import dao.VentaDAO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Cliente;
import modelo.DetalleVenta;
import modelo.Inventario;
import modelo.Venta;

/**
 *
 * @author natha
 */
public class ControladorVentas {

    private final VistaVentas vista;
    private final VentaDAO ventaDAO;
    private final ClienteDAO clienteDAO;
    private final InventarioDAO inventarioDAO;

    /** Caché de inventarios para acceder al idProducto al agregar detalles. */
    private List<Inventario> inventariosCache = new ArrayList<>();

    /** Lista temporal de detalles mientras se arma la venta. */
    private final List<DetalleVenta> detallesTemp = new ArrayList<>();

    public ControladorVentas(VistaVentas vista) {
        this.vista = vista;
        this.ventaDAO = new VentaDAO();
        this.clienteDAO = new ClienteDAO();
        this.inventarioDAO = new InventarioDAO();

        inicializarFormulario();
        iniciarEventos();
    }

    private void inicializarFormulario() {
        // Fecha y hora actuales (editables por si el usuario necesita corregirlas)
        vista.txtFecha.setText(LocalDate.now().toString());
        vista.txtHora.setText(LocalTime.now().withNano(0).toString());

        // SOLO estos dos campos son de solo lectura (los calcula el sistema)
        vista.txtSubtotal.setEditable(false);
        vista.txtTotal.setEditable(false);

        // ID Venta lo asigna la BD automáticamente
        vista.txtIdVenta.setEditable(false);

        // Nombre y apellido se autocompletan al elegir cliente
        vista.txtNombreCli.setEditable(false);
        vista.txtApellidoCli.setEditable(false);

        cargarClientes();
        cargarProductos();
    }

    private void iniciarEventos() {
        vista.cmbCliente.addActionListener(e -> autocompletarCliente());
        vista.btnAgregar.addActionListener(e -> agregarDetalle());
        vista.btnQuitar.addActionListener(e -> quitarDetalle());
        vista.btnRegistrar.addActionListener(e -> registrarVenta());
        vista.btnCancelar.addActionListener(e -> cancelar());
        // vista.btnBack.addActionListener(e -> volverMenu());
        vista.btnImprimir
                .addActionListener(e -> JOptionPane.showMessageDialog(vista, "Función de impresión en desarrollo."));
    }

    private void cargarClientes() {
        vista.cmbCliente.removeAllItems();
        vista.cmbCliente.addItem("-- Seleccione --");
        try {
            List<Cliente> clientes = clienteDAO.listar();
            for (Cliente c : clientes) {
                vista.cmbCliente.addItem(
                        c.getIdCliente() + " - " + c.getNombre() + " " + c.getApellido());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "Error al cargar clientes: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarProductos() {
        vista.cmbProducto.removeAllItems();
        vista.cmbProducto.addItem("-- Seleccione --");
        try {
            inventariosCache = inventarioDAO.listar();
            if (inventariosCache.isEmpty()) {
                JOptionPane.showMessageDialog(vista,
                        "No hay productos en inventario. Agregue productos primero.",
                        "Inventario vacío", JOptionPane.WARNING_MESSAGE);
                return;
            }
            for (Inventario i : inventariosCache) {
                vista.cmbProducto.addItem(
                        i.getIdInventario() + " - " + i.getNombreProducto()
                                + " ($" + i.getPrecioUnitario() + ")"
                                + "  [stock: " + i.getStockDisponible() + "]");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "Error al cargar productos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void autocompletarCliente() {
        String sel = (String) vista.cmbCliente.getSelectedItem();
        if (sel == null || sel.startsWith("--")) {
            vista.txtNombreCli.setText("");
            vista.txtApellidoCli.setText("");
            return;
        }
        try {
            int idCliente = Integer.parseInt(sel.split(" - ")[0].trim());
            Cliente c = clienteDAO.buscar(idCliente);
            if (c != null) {
                vista.txtNombreCli.setText(c.getNombre());
                vista.txtApellidoCli.setText(c.getApellido());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "Error al obtener cliente: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarDetalle() {
        int indexCombo = vista.cmbProducto.getSelectedIndex();
        if (indexCombo <= 0) {
            JOptionPane.showMessageDialog(vista, "Seleccione un producto.");
            return;
        }

        String cantStr = vista.txtCantidad.getText().trim();
        if (cantStr.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese la cantidad.");
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantStr);
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(vista, "La cantidad debe ser mayor a 0.");
                return;
            }

            // indexCombo - 1 porque el combo tiene "-- Seleccione --" en posición 0
            Inventario inv = inventariosCache.get(indexCombo - 1);

            if (inv.getStockDisponible() < cantidad) {
                JOptionPane.showMessageDialog(vista,
                        "Stock insuficiente. Disponible: " + inv.getStockDisponible(),
                        "Sin stock", JOptionPane.WARNING_MESSAGE);
                return;
            }

            DetalleVenta d = new DetalleVenta();
            d.setIdProducto(inv.getIdProducto()); // FK real hacia tabla productos
            d.setCantidad(cantidad);
            d.setPrecioUnitario(inv.getPrecioUnitario());

            detallesTemp.add(d);
            refrescarTablaDetalle();
            actualizarTotales();

            vista.cmbProducto.setSelectedIndex(0);
            vista.txtCantidad.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista,
                    "Cantidad inválida. Ingrese un número entero.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quitarDetalle() {
        int fila = vista.tblDetalleVenta.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(vista, "Seleccione una fila de la tabla para quitar.");
            return;
        }
        detallesTemp.remove(fila);
        refrescarTablaDetalle();
        actualizarTotales();
    }

    private void refrescarTablaDetalle() {
        DefaultTableModel modelo = (DefaultTableModel) vista.tblDetalleVenta.getModel();
        modelo.setRowCount(0);
        for (DetalleVenta d : detallesTemp) {
            modelo.addRow(new Object[] {
                    d.getIdProducto(),
                    d.getCantidad(),
                    d.getPrecioUnitario(),
                    d.getSubtotal()
            });
        }
    }

    private void actualizarTotales() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (DetalleVenta d : detallesTemp) {
            subtotal = subtotal.add(d.getSubtotal());
        }
        vista.txtSubtotal.setText(subtotal.setScale(2).toPlainString());
        vista.txtTotal.setText(subtotal.setScale(2).toPlainString());
    }

    private void registrarVenta() {
        if (!validarFormulario())
            return;

        int confirm = JOptionPane.showConfirmDialog(vista,
                "¿Confirmar el registro de esta venta?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        try {
            String selCliente = (String) vista.cmbCliente.getSelectedItem();
            int idCliente = Integer.parseInt(selCliente.split(" - ")[0].trim());

            Venta venta = new Venta();
            venta.setFecha(LocalDate.parse(vista.txtFecha.getText().trim()));
            venta.setHora(LocalTime.parse(vista.txtHora.getText().trim()));
            venta.setIdCliente(idCliente);
            venta.setSubtotal(new BigDecimal(vista.txtSubtotal.getText()));
            venta.setTotal(new BigDecimal(vista.txtTotal.getText()));
            venta.setDetalles(new ArrayList<>(detallesTemp));

            ventaDAO.registrarVenta(venta);

            JOptionPane.showMessageDialog(vista, "Venta registrada correctamente.");
            cancelar();
            cargarProductos(); // refrescar stock actualizado en el combo

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "Error al registrar la venta: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarFormulario() {
        String selCliente = (String) vista.cmbCliente.getSelectedItem();
        if (selCliente == null || selCliente.startsWith("--")) {
            JOptionPane.showMessageDialog(vista, "Seleccione un cliente.",
                    "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (vista.txtFecha.getText().trim().isEmpty()
                || vista.txtHora.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Fecha y hora son requeridos.",
                    "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (detallesTemp.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Agregue al menos un producto al detalle.",
                    "Sin productos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void cancelar() {
        detallesTemp.clear();
        ((DefaultTableModel) vista.tblDetalleVenta.getModel()).setRowCount(0);
        vista.cmbCliente.setSelectedIndex(0);
        vista.cmbProducto.setSelectedIndex(0);
        vista.txtNombreCli.setText("");
        vista.txtApellidoCli.setText("");
        vista.txtCantidad.setText("");
        vista.txtSubtotal.setText("");
        vista.txtTotal.setText("");
        vista.txtFecha.setText(LocalDate.now().toString());
        vista.txtHora.setText(LocalTime.now().withNano(0).toString());
    }

    private void volverMenu() {
        vista.dispose();
    }
}
