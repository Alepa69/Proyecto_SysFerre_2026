package controlador;

import com.ues.group.arbolb.ArbolBusqueda;
import com.ues.group.vista.VistaHistorialVentas;
import dao.VentaDAO;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.DetalleVenta;
import modelo.Venta;

/**
 *
 * @author natha
 */
public class ControladorHistorialVentas {

    private final VistaHistorialVentas vista;
    private final VentaDAO ventaDAO;

    private ArbolBusqueda<Venta> arbolVentas; // ← reemplaza ventasCache

    public ControladorHistorialVentas(VistaHistorialVentas vista) {
        this.vista = vista;
        this.ventaDAO = new VentaDAO();
        inicializarTablas();
        iniciarEventos();
        cargarTodasLasVentas();
    }

    private void inicializarTablas() {
        vista.tbVentas.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Cliente", "Fecha", "Total de venta"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        vista.tbDetalleVenta.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Prod.", "Producto", "Cantidad", "Precio Unit.", "Sub Total"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        vista.tbVentas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }

    private void iniciarEventos() {
        vista.btnRefreshTbVentas.addActionListener(e -> cargarTodasLasVentas());
        vista.btnBuscarVenta.addActionListener(e -> buscarVenta());

        vista.tbVentas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                verDetalleVenta(); // silencioso, sin dialog
            }
        });
    }

    private void cargarTodasLasVentas() {
        try {
            arbolVentas = ventaDAO.listar();
            poblarTablaVentas(arbolVentas.IND());
            limpiarPanelDetalle();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "Error al cargar ventas:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void poblarTablaVentas(List<Venta> ventas) {
        DefaultTableModel modelo = (DefaultTableModel) vista.tbVentas.getModel();
        modelo.setRowCount(0);
        for (Venta v : ventas) {
            String nombreCliente = (v.getCliente() != null)
                    ? v.getCliente().getNombre() + " " + v.getCliente().getApellido()
                    : "ID " + v.getIdCliente();

            modelo.addRow(new Object[]{
                v.getIdVenta(),
                nombreCliente,
                v.getFecha(),
                "$" + v.getTotal().setScale(2)
            });
        }
    }

    private void buscarVenta() {
        String criterio = vista.cbBusquedaVenta.getSelectedItem().toString();
        String texto = vista.txtBuscarVenta.getText().trim().toLowerCase();

        if (texto.isEmpty()) {
            poblarTablaVentas(arbolVentas.IND());
            limpiarPanelDetalle();
            return;
        }

        List<Venta> filtradas = new java.util.ArrayList<>();
        for (Venta v : arbolVentas.IND()) {
            switch (criterio) {
                case "ID" -> {
                    if (String.valueOf(v.getIdVenta()).contains(texto)) {
                        filtradas.add(v);
                    }
                }
                case "Nombre" -> {
                    if (v.getCliente() != null
                            && v.getCliente().getNombre().toLowerCase().contains(texto)) {
                        filtradas.add(v);
                    }
                }
                case "Apellido" -> {
                    if (v.getCliente() != null
                            && v.getCliente().getApellido().toLowerCase().contains(texto)) {
                        filtradas.add(v);
                    }
                }
            }
        }

        poblarTablaVentas(filtradas);
        limpiarPanelDetalle();

        if (filtradas.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "No se encontraron ventas con ese criterio.",
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void verDetalleVenta() {
        int fila = vista.tbVentas.getSelectedRow();
        if (fila < 0) {
            return; 
        }

        int idVenta = (int) vista.tbVentas.getValueAt(fila, 0);

        try {
            List<DetalleVenta> detalles = ventaDAO.listarDetalles(idVenta);

            // actualizar panel detalle
            DefaultTableModel modeloDetalle = (DefaultTableModel) vista.tbDetalleVenta.getModel();
            modeloDetalle.setRowCount(0);
            for (DetalleVenta d : detalles) {
                modeloDetalle.addRow(new Object[]{
                    d.getIdProducto(),
                    d.getProducto() != null ? d.getProducto().getDescripcion() : "—",
                    d.getCantidad(),
                    "$" + d.getPrecioUnitario().setScale(2),
                    "$" + d.getSubtotal().setScale(2)
                });
            }

            // actualizar labels del panel detalle
            String cliente = (String) vista.tbVentas.getValueAt(fila, 1);
            String total   = (String) vista.tbVentas.getValueAt(fila, 3);
            vista.lblClienteVenta.setText("Cliente: " + cliente);
            vista.lblTotalVenta.setText("TOTAL DE LA VENTA: " + total);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                "Error al cargar detalle:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarPanelDetalle() {
        ((DefaultTableModel) vista.tbDetalleVenta.getModel()).setRowCount(0);
        vista.lblClienteVenta.setText("Cliente: ----------------");
        vista.lblTotalVenta.setText("TOTAL DE LA VENTA: ------");
    }
}