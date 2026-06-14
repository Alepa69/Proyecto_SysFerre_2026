
package controlador;

import com.ues.group.vista.VistaVentas;

import Arboles.ArbolBusqueda;
import dao.ClienteDAO;
import dao.InventarioDAO;
import dao.VentaDAO;
import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
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

    private static final int STOCK_BAJO = 5;

    private final VistaVentas vista;
    private final VentaDAO ventaDAO;
    private final ClienteDAO clienteDAO;
    private final InventarioDAO inventarioDAO;

    // Inventarios su stockDisponible se modifica localmente al armar la venta
    private List<Inventario> inventariosCache = new ArrayList<>();

    // detalles temp
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
        vista.txtIdVenta.setEditable(false);
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
        vista.btnBack.addActionListener(e -> volverMenu());
        vista.btnImprimir
                .addActionListener(e -> JOptionPane.showMessageDialog(vista, "Función de impresión en desarrollo."));
    }

    // cargar datos
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
            e.printStackTrace();
        }
    }

    private void cargarProductos() {
        vista.cmbProducto.removeAllItems();
        vista.cmbProducto.addItem("-- Seleccione --");
        try {
            inventariosCache = inventarioDAO.listar();

            for (Inventario inv : inventariosCache) {
                String etiqueta = construirEtiquetaProducto(inv);
                vista.cmbProducto.addItem(etiqueta);
            }

            // Renderer que colorea y deshabilita visualmente los agotados
            vista.cmbProducto.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {

                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    // index 0 =Seleccione, índices 1..N corresponden a inventariosCache[index-1]
                    if (index > 0 && index <= inventariosCache.size()) {
                        Inventario inv = inventariosCache.get(index - 1);
                        if (inv.getStockDisponible() == 0) {
                            setForeground(Color.RED);
                            setEnabled(false); // no clickeable
                        } else if (inv.getStockDisponible() <= STOCK_BAJO) {
                            setForeground(new Color(200, 100, 0)); // naranja oscuro
                        } else {
                            setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
                        }
                    }
                    return this;
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "Error al cargar productos:\n" + e.getClass().getSimpleName() + ": " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Etiqueta cbox
    private String construirEtiquetaProducto(Inventario inv) {
        String base = inv.getIdInventario() + " - " + inv.getNombreProducto()
                + " ($" + inv.getPrecioUnitario() + ")";
        if (inv.getStockDisponible() == 0) {
            return base + "  [AGOTADO]";
        } else if (inv.getStockDisponible() <= STOCK_BAJO) {
            return base + "  [POCO STOCK: " + inv.getStockDisponible() + "]";
        } else {
            return base + "  [stock: " + inv.getStockDisponible() + "]";
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
            JOptionPane.showMessageDialog(vista,
                    "Cantidad inválida. Ingrese un número entero.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(vista, "La cantidad debe ser mayor a 0.",
                    "Valor inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // indexCombo - 1 porque posición 0 es seleccione
        Inventario inv = inventariosCache.get(indexCombo - 1);

        // Validar stock disponible
        if (inv.getStockDisponible() == 0) {
            JOptionPane.showMessageDialog(vista,
                    "El producto \"" + inv.getNombreProducto() + "\" está AGOTADO.",
                    "Sin stock", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (inv.getStockDisponible() < cantidad) {
            JOptionPane.showMessageDialog(vista,
                    "Stock insuficiente para \"" + inv.getNombreProducto() + "\".\n" +
                            "Disponible: " + inv.getStockDisponible() + "  |  Solicitado: " + cantidad,
                    "Stock insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // detalle con subtotal calculado
        DetalleVenta d = new DetalleVenta();
        d.setIdProducto(inv.getIdProducto());
        d.setCantidad(cantidad);
        d.setPrecioUnitario(inv.getPrecioUnitario());

        detallesTemp.add(d);

        inv.setStockDisponible(inv.getStockDisponible() - cantidad);

        // Actualizar combo nuevo stock
        vista.cmbProducto.removeItemAt(indexCombo);
        vista.cmbProducto.insertItemAt(construirEtiquetaProducto(inv), indexCombo);

        refrescarTablaDetalle();
        actualizarTotales();

        vista.cmbProducto.setSelectedIndex(0);
        vista.txtCantidad.setText("");
    }

    private void quitarDetalle() {
        int fila = vista.tblDetalleVenta.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione una fila de la tabla para quitar.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DetalleVenta d = detallesTemp.remove(fila);

        // Devolver stock al cache buscando por idProducto
        for (int i = 0; i < inventariosCache.size(); i++) {
            if (inventariosCache.get(i).getIdProducto() == d.getIdProducto()) {
                Inventario inv = inventariosCache.get(i);
                inv.setStockDisponible(inv.getStockDisponible() + d.getCantidad());
                // Actualizar etiqueta en combo (posición i+1 porque 0 es "-- Seleccione --")
                vista.cmbProducto.removeItemAt(i + 1);
                vista.cmbProducto.insertItemAt(construirEtiquetaProducto(inv), i + 1);
                break;
            }
        }

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
                "Confirmar venta", JOptionPane.YES_NO_OPTION);
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

            JOptionPane.showMessageDialog(vista,
                    "Venta registrada correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
            cargarProductos(); // recargar stock real desde BD

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "Error al registrar la venta:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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
        cargarProductos(); // restaurar stock real desde BD al cancelar
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
    }

    private void volverMenu() {
        vista.dispose();
    }
}
