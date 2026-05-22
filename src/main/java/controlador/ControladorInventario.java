package controlador;

import com.ues.group.arbolb.ArbolB;
import com.ues.group.vista.VistaInventario;
import dao.InventarioDAO;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Inventario;

/**
 *
 * @author mendo
 */
public class ControladorInventario {

    private static final int ORDEN_ARBOL = 3;

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

    private void cargarTabla() {
        try {
            List<Inventario> lista = dao.listar();
            llenarTabla(lista);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    vista,
                    "Error al cargar inventario: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void buscar() {

        try {

            String texto = vista.txtBuscar.getText().trim().toLowerCase();

            List<Inventario> lista = dao.listar();
            List<Inventario> filtrada = new ArrayList<>();

            boolean esNumero = texto.matches("\\d+");

            for (Inventario inv : lista) {

                if (esNumero) {

                    if (inv.getIdInventario() == Integer.parseInt(texto)) {
                        filtrada.add(inv);
                    }

                } else {

                    if (inv.getNombreProducto() != null
                            && inv.getNombreProducto()
                                    .toLowerCase()
                                    .contains(texto)) {

                        filtrada.add(inv);
                    }
                }
            }

            llenarTabla(filtrada);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    vista,
                    "Error al buscar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void ordenarTabla() {

        try {

            List<Inventario> lista = dao.listar();

            String criterio = vista.cmbOrdenar.getSelectedItem() == null
                    ? ""
                    : vista.cmbOrdenar.getSelectedItem().toString();

            List<Inventario> ordenada = ordenarConArbolB(lista, criterio);

            llenarTabla(ordenada);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    vista,
                    "Error al ordenar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private List<Inventario> ordenarConArbolB(
            List<Inventario> lista,
            String criterio
    ) {

        ArbolB<NodoInventario> arbol = new ArbolB<>(ORDEN_ARBOL);

        for (Inventario inv : lista) {
            arbol.insertar(new NodoInventario(inv, criterio));
        }

        List<Inventario> resultado = new ArrayList<>();

        for (NodoInventario nodo : arbol.recorridoEnOrden()) {
            resultado.add(nodo.inventario);
        }

        if ("Stock Descendente".equals(criterio)) {
            Collections.reverse(resultado);
        }

        return resultado;
    }

    private void llenarTabla(List<Inventario> lista) {

        DefaultTableModel modelo
                = (DefaultTableModel) vista.tblInventario.getModel();

        modelo.setRowCount(0);

        for (Inventario inv : lista) {

            modelo.addRow(new Object[]{
                inv.getIdInventario(),
                inv.getIdProducto(),
                inv.getNombreProducto(),
                inv.getPrecioUnitario(),
                inv.getDescripcion(),
                inv.getStockDisponible()
            });
        }
    }

    private void exportar() {

        DefaultTableModel modelo
                = (DefaultTableModel) vista.tblInventario.getModel();

        if (modelo.getRowCount() == 0) {

            JOptionPane.showMessageDialog(
                    vista,
                    "No hay datos para exportar.",
                    "Exportar",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle("Guardar inventario como CSV");
        chooser.setSelectedFile(new File("inventario.csv"));

        if (chooser.showSaveDialog(vista)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = chooser.getSelectedFile();

        if (!archivo.getName().toLowerCase().endsWith(".csv")) {

            archivo = new File(
                    archivo.getAbsolutePath() + ".csv"
            );
        }

        try (FileWriter fw = new FileWriter(archivo)) {

            int columnas = modelo.getColumnCount();

            // Encabezados
            for (int c = 0; c < columnas; c++) {

                fw.append(escapar(modelo.getColumnName(c)));

                if (c < columnas - 1) {
                    fw.append(',');
                }
            }

            fw.append('\n');

            // Filas
            for (int f = 0; f < modelo.getRowCount(); f++) {

                for (int c = 0; c < columnas; c++) {

                    Object valor = modelo.getValueAt(f, c);

                    fw.append(
                            escapar(valor == null
                                    ? ""
                                    : valor.toString())
                    );

                    if (c < columnas - 1) {
                        fw.append(',');
                    }
                }

                fw.append('\n');
            }

            JOptionPane.showMessageDialog(
                    vista,
                    "Inventario exportado a:\n"
                    + archivo.getAbsolutePath(),
                    "Exportar",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    vista,
                    "No se pudo exportar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String escapar(String valor) {

        if (valor.contains(",")
                || valor.contains("\"")
                || valor.contains("\n")) {

            return "\""
                    + valor.replace("\"", "\"\"")
                    + "\"";
        }

        return valor;
    }

    private static class NodoInventario
            implements Comparable<NodoInventario> {

        final Inventario inventario;
        final String criterio;

        NodoInventario(Inventario inventario, String criterio) {
            this.inventario = inventario;
            this.criterio = criterio;
        }

        @Override
        public int compareTo(NodoInventario otro) {

            int c;

            switch (criterio) {

                case "Nombre A-Z" ->

                    c = compararTexto(
                            inventario.getNombreProducto(),
                            otro.inventario.getNombreProducto()
                    );

                case "Precio" ->

                    c = compararPrecio(
                            inventario.getPrecioUnitario(),
                            otro.inventario.getPrecioUnitario()
                    );

                case "Stock Ascendente", "Stock Descendente" ->

                    c = Integer.compare(
                            inventario.getStockDisponible(),
                            otro.inventario.getStockDisponible()
                    );

                default ->

                    c = Integer.compare(
                            inventario.getIdInventario(),
                            otro.inventario.getIdInventario()
                    );
            }

            if (c == 0) {

                c = Integer.compare(
                        inventario.getIdInventario(),
                        otro.inventario.getIdInventario()
                );
            }

            return c;
        }

        private int compararTexto(String a, String b) {

            if (a == null) {
                a = "";
            }

            if (b == null) {
                b = "";
            }

            return a.compareToIgnoreCase(b);
        }

        private int compararPrecio(BigDecimal a, BigDecimal b) {

            if (a == null) {
                a = BigDecimal.ZERO;
            }

            if (b == null) {
                b = BigDecimal.ZERO;
            }

            return a.compareTo(b);
        }
    }
}
