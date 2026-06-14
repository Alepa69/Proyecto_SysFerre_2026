package controlador;

import Arboles.ArbolBusqueda;
import Arboles.Nodo;
import com.ues.group.vista.VistaInventario;
import dao.InventarioDAO;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Inventario;

/**
 *
 * @author mendo
 */
public class ControladorInventario {

    private final VistaInventario vista;
    private final InventarioDAO dao;

    public ControladorInventario(VistaInventario vista) {
        this.vista = vista;
        this.dao = new InventarioDAO();

        iniciarEventos();
        cargarTabla();
    }

    private void iniciarEventos() {
        vista.btnBuscar.addActionListener(e -> buscar());
        vista.txtBuscar.addActionListener(e -> buscar());
        vista.btnOrdenar.addActionListener(e -> ordenarTabla());
        vista.btnActualizar.addActionListener(e -> {
            vista.txtBuscar.setText("");
            vista.cmbOrdenar.setSelectedIndex(0);
            cargarTabla();
        });
        vista.btnExportar.addActionListener(e -> exportar());
        vista.btnRegresar.addActionListener(e -> vista.dispose());
    }

    private String criterioSeleccionado() {
        return vista.cmbOrdenar.getSelectedItem() == null
                ? "ID"
                : vista.cmbOrdenar.getSelectedItem().toString();
    }

    private void cargarTabla() {
        mostrarOrdenado(criterioSeleccionado());
    }

    private void ordenarTabla() {
        mostrarOrdenado(criterioSeleccionado());
    }

    private void mostrarOrdenado(String criterio) {
        try {
            ArbolBusqueda<Inventario> arbol = dao.listarOrdenado(criterio);
            DefaultTableModel modelo = (DefaultTableModel) vista.tblInventario.getModel();
            modelo.setRowCount(0);

            if ("Stock Descendente".equals(criterio)) {
                recorrerDescendente(arbol.getRaiz(), modelo); // derecha-nodo-izquierda
            } else {
                recorrerInOrden(arbol.getRaiz(), modelo); // izquierda-nodo-derecha
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "Error al cargar inventario: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        try {
            String texto = vista.txtBuscar.getText().trim();
            DefaultTableModel modelo = (DefaultTableModel) vista.tblInventario.getModel();
            modelo.setRowCount(0);

            if (texto.matches("\\d+")) {
                Inventario inv = dao.buscar(Integer.parseInt(texto));
                if (inv != null) {
                    agregarFila(modelo, inv);
                }
            } else {
                ArbolBusqueda<Inventario> arbol = dao.buscarPorNombre(texto);
                recorrerInOrden(arbol.getRaiz(), modelo);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "Error al buscar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recorrerInOrden(Nodo r, DefaultTableModel modelo) {
        if (r != null) {
            recorrerInOrden(r.getRamaIzq(), modelo);
            agregarFila(modelo, (Inventario) r.getDato());
            recorrerInOrden(r.getRamaDrch(), modelo);
        }
    }

    private void recorrerDescendente(Nodo r, DefaultTableModel modelo) {
        if (r != null) {
            recorrerDescendente(r.getRamaDrch(), modelo);
            agregarFila(modelo, (Inventario) r.getDato());
            recorrerDescendente(r.getRamaIzq(), modelo);
        }
    }

    private void agregarFila(DefaultTableModel modelo, Inventario inv) {
        modelo.addRow(new Object[] {
                inv.getIdInventario(),
                inv.getIdProducto(),
                inv.getNombreProducto(),
                inv.getPrecioUnitario(),
                inv.getDescripcion(),
                inv.getStockDisponible()
        });
    }

    private void exportar() {
        DefaultTableModel modelo = (DefaultTableModel) vista.tblInventario.getModel();
        if (modelo.getRowCount() == 0) {
            JOptionPane.showMessageDialog(vista, "No hay datos para exportar.",
                    "Exportar", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar inventario como CSV");
        chooser.setSelectedFile(new File("inventario.csv"));
        if (chooser.showSaveDialog(vista) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = chooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".csv")) {
            archivo = new File(archivo.getAbsolutePath() + ".csv");
        }

        try (FileWriter fw = new FileWriter(archivo)) {
            int columnas = modelo.getColumnCount();
            for (int c = 0; c < columnas; c++) {
                fw.append(escapar(modelo.getColumnName(c)));
                if (c < columnas - 1) {
                    fw.append(',');
                }
            }
            fw.append('\n');
            for (int f = 0; f < modelo.getRowCount(); f++) {
                for (int c = 0; c < columnas; c++) {
                    Object valor = modelo.getValueAt(f, c);
                    fw.append(escapar(valor == null ? "" : valor.toString()));
                    if (c < columnas - 1) {
                        fw.append(',');
                    }
                }
                fw.append('\n');
            }
            JOptionPane.showMessageDialog(vista,
                    "Inventario exportado a:\n" + archivo.getAbsolutePath(),
                    "Exportar", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "No se pudo exportar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String escapar(String valor) {
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
