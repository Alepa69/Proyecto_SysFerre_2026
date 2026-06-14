
package controlador;

import Arboles.ArbolBusqueda;
import com.ues.group.vista.VistaHistorialVentas;
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
import modelo.Producto;
import modelo.Venta;

public class ControladorVentas {

    private static final int STOCK_BAJO = 5;

    private final VistaVentas vista;
    private final VentaDAO ventaDAO;
    private final ClienteDAO clienteDAO;
    private final InventarioDAO inventarioDAO;

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
        vista.txtFecha.setText(LocalDate.now().toString());
        vista.txtHora.setText(LocalTime.now().withNano(0).toString());

        vista.txtSubtotal.setEditable(false);
        vista.txtTotal.setEditable(false);
        vista.txtNombreCli.setEditable(false);
        vista.txtApellidoCli.setEditable(false);

        limpiarLabelsProducto();
        cargarClientes();
        cargarProductos();
    }

    private void iniciarEventos() {
        vista.cmbCliente.addActionListener(e -> autocompletarCliente());
        vista.cmbProducto.addActionListener(e -> actualizarLabelsProducto());
        vista.btnAgregar.addActionListener(e -> agregarDetalle());
        vista.btnQuitar.addActionListener(e -> quitarDetalle());
        vista.btnRegistrar.addActionListener(e -> registrarVenta());
        vista.btnCancelar.addActionListener(e -> cancelar());
        vista.btnBack.addActionListener(e -> volverMenu());
        vista.btnHistorialVenta.addActionListener(e -> abrirHistorial());
    }

    private void actualizarLabelsProducto() {
        String item = (String) vista.cmbProducto.getSelectedItem();
        if (item == null || item.startsWith("--")) {
            limpiarLabelsProducto();
            return;
        }
        try {
            int idInventario = Integer.parseInt(item.split(" - ")[0].trim());
            Inventario inv = inventarioDAO.buscar(idInventario);

            vista.lblPrecio.setText("Precio Unitario: $" + inv.getPrecioUnitario().setScale(2));

            int stock = inv.getStockDisponible();
            if (stock == 0) {
                vista.lblStock.setText("Stock Disponible: AGOTADO");
                vista.lblStock.setForeground(java.awt.Color.RED);
            } else if (stock <= STOCK_BAJO) {
                vista.lblStock.setText("Stock Disponible: " + stock + " (poco stock)");
                vista.lblStock.setForeground(new java.awt.Color(200, 100, 0));
            } else {
                vista.lblStock.setText("Stock Disponible: " + stock);
                vista.lblStock.setForeground(vista.lblPrecio.getForeground());
            }
        } catch (Exception e) {
            limpiarLabelsProducto();
        }
    }

    private void limpiarLabelsProducto() {
        vista.lblPrecio.setText("Precio Unitario: -----------");
        vista.lblStock.setText("Stock Disponible: -----------");
        vista.lblStock.setForeground(vista.lblPrecio.getForeground());
    }

    private void cargarClientes() {
        vista.cmbCliente.removeAllItems();
        vista.cmbCliente.addItem("-- Seleccione --");
        try {
            ArbolBusqueda<Cliente> arbol = clienteDAO.listar();
            for (Cliente c : arbol.IND()) {
                vista.cmbCliente.addItem(
                        c.getIdCliente() + " - " + c.getNombre() + " " + c.getApellido());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "Error al cargar clientes:\n" + e.getClass().getSimpleName() + ": " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarProductos() {
        vista.cmbProducto.removeAllItems();
        vista.cmbProducto.addItem("-- Seleccione --");
        try {
            List<Inventario> lista = inventarioDAO.listar();
            for (Inventario inv : lista) {
                vista.cmbProducto.addItem(inv.getIdInventario() + " - " + inv.getNombreProducto());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "Error al cargar productos:\n" + e.getClass().getSimpleName() + ": " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        limpiarLabelsProducto();
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
        String item = (String) vista.cmbProducto.getSelectedItem();
        if (item == null || item.startsWith("--")) {
            JOptionPane.showMessageDialog(vista, "Seleccione un producto.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cantStr = vista.txtCantidad.getText().trim();
        if (cantStr.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese la cantidad.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Cantidad inválida. Ingrese un número entero.",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(vista, "La cantidad debe ser mayor a 0.",
                    "Valor inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idInventario = Integer.parseInt(item.split(" - ")[0].trim());
            Inventario inv = inventarioDAO.buscar(idInventario);

            if (inv.getStockDisponible() == 0) {
                JOptionPane.showMessageDialog(vista,
                        "El producto \"" + inv.getNombreProducto() + "\" está AGOTADO.",
                        "Sin stock", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (cantidad > inv.getStockDisponible()) {
                JOptionPane.showMessageDialog(vista,
                        "Stock insuficiente para \"" + inv.getNombreProducto() + "\".\n"
                                + "Stock disponible: " + inv.getStockDisponible() + "\n"
                                + "Cantidad solicitada: " + cantidad,
                        "Stock insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Producto p = new Producto();
            p.setIdProducto(inv.getIdProducto());
            p.setDescripcion(inv.getNombreProducto());

            DetalleVenta d = new DetalleVenta();
            d.setIdProducto(inv.getIdProducto());
            d.setProducto(p);
            d.setCantidad(cantidad);
            d.setPrecioUnitario(inv.getPrecioUnitario());

            detallesTemp.add(d);
            actualizarLabelsProducto();
            refrescarTablaDetalle();
            actualizarTotales();

            vista.cmbProducto.setSelectedIndex(0);
            vista.txtCantidad.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "Error al agregar producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quitarDetalle() {
        int fila = vista.tblDetalleVenta.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(vista, "Seleccione una fila de la tabla para quitar.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        detallesTemp.remove(fila);
        actualizarLabelsProducto();
        refrescarTablaDetalle();
        actualizarTotales();
    }

    private void refrescarTablaDetalle() {
        DefaultTableModel modelo = (DefaultTableModel) vista.tblDetalleVenta.getModel();
        modelo.setRowCount(0);
        for (DetalleVenta d : detallesTemp) {
            modelo.addRow(new Object[] {
                    d.getIdProducto(),
                    d.getProducto() != null ? d.getProducto().getDescripcion() : "—",
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
        if (!validarFormulario()) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(vista,
                "¿Confirmar el registro de esta venta?",
                "Confirmar venta", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

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

            JOptionPane.showMessageDialog(vista,
                    "Venta registrada correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
            cargarProductos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "Error al registrar la venta:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarFormulario() {
        String selCliente = (String) vista.cmbCliente.getSelectedItem();
        if (selCliente == null || selCliente.startsWith("--")) {
            JOptionPane.showMessageDialog(vista, "Seleccione un cliente.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (vista.txtFecha.getText().trim().isEmpty() || vista.txtHora.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Fecha y hora son requeridos.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
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
        limpiarFormulario();
        cargarProductos();
    }

    private void limpiarFormulario() {
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
        limpiarLabelsProducto();
    }

    private void volverMenu() {
        vista.dispose();
    }

    private void abrirHistorial() {
        VistaHistorialVentas vistaHistorial = new VistaHistorialVentas();
        new ControladorHistorialVentas(vistaHistorial);
        vistaHistorial.setVisible(true);
    }
}
