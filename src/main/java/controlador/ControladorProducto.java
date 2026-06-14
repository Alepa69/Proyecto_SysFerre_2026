package controlador;

import Arboles.ArbolAVL;
import com.ues.group.vista.VistaProductos;
import dao.ProductoDAO;
import modelo.Producto;
import Arboles.ArbolBusqueda;

import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ControladorProducto {

    private final VistaProductos vista;
    private final ProductoDAO dao;
    private ArbolAVL<Producto> arbolBase;
    // arbol principal refleja datos de la bd

    public ControladorProducto(VistaProductos vista) {
        this.vista = vista;
        this.dao = new ProductoDAO();
        iniciarEventos();
        cargarTabla();
    }

    private void iniciarEventos() {
        vista.btnNuevo.addActionListener(e -> nuevo());
        vista.btnGuardar.addActionListener(e -> guardar());
        vista.btnModificar.addActionListener(e -> modificar());
        vista.btnEliminar.addActionListener(e -> eliminar());
        vista.btnLimpiar.addActionListener(e -> limpiar());
        vista.btnBack.addActionListener(e -> backMenu());
        vista.btnBuscar.addActionListener(e -> buscar());

        vista.tblProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                seleccionarFila();
            }
        });
    }

    private void nuevo() {
        limpiar();
        vista.txtDescripcion.requestFocus();
    }

    private void backMenu() {
        vista.dispose();
    }

    // implementacion busqueda con arbol
    private void buscar() {
        String texto = vista.txtId.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese un ID para buscar.",
                    "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idBuscado;
        try {
            idBuscado = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "El ID debe ser un número entero.",
                    "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (arbolBase == null || arbolBase.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No hay productos cargados.",
                    "Sin datos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // liista ordenada del arbol principal en inorden
        List<Producto> listaActual = arbolBase.IND();

        // arbol auxiliar solo con los id para facilitar la buusqueda
        ArbolBusqueda<Integer> arbolIds = new ArbolBusqueda<>();
        for (Producto p : listaActual) {
            arbolIds.insertar(p.getIdProducto());
        }

        if (arbolIds.buscar(idBuscado) == null) {
            JOptionPane.showMessageDialog(vista,
                    "No se encontró producto con ID " + idBuscado + ".",
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // si existe recupera el producto completo
        Producto resultado = listaActual.stream()
                .filter(p -> p.getIdProducto() == idBuscado)
                .findFirst()
                .orElse(null);

        if (resultado != null) {
            // muestra solo ese producto en la tabla
            DefaultTableModel modelo = (DefaultTableModel) vista.tblProductos.getModel();
            modelo.setRowCount(0);
            modelo.addRow(new Object[] {
                    resultado.getIdProducto(),
                    resultado.getPrecio(),
                    resultado.getTipo(),
                    resultado.getStock(),
                    resultado.getDescripcion()
            });
            vista.txtId.setText(String.valueOf(resultado.getIdProducto()));
            vista.txtPrecio.setText(resultado.getPrecio().toString());
            vista.cmbTipo.setSelectedItem(resultado.getTipo());
            vista.txtStock.setText(String.valueOf(resultado.getStock()));
            vista.txtDescripcion.setText(resultado.getDescripcion());
        }
    }

    // crud
    private void guardar() {
        if (!validarCampos())
            return;
        try {
            Producto p = construirDesdeVista();
            dao.insertar(p);
            JOptionPane.showMessageDialog(vista, "Producto guardado correctamente.");
            limpiar();
            cargarTabla();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista,
                    "Precio y Stock deben ser valores numéricos válidos.",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al guardar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificar() {
        if (vista.txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione un producto de la tabla.");
            return;
        }
        if (!validarCampos())
            return;
        try {
            Producto p = construirDesdeVista();
            p.setIdProducto(Integer.parseInt(vista.txtId.getText()));
            dao.actualizar(p);
            JOptionPane.showMessageDialog(vista, "Producto modificado correctamente.");
            limpiar();
            cargarTabla();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista,
                    "Precio y Stock deben ser valores numéricos válidos.",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al modificar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (vista.txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione un producto de la tabla.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(vista,
                "¿Está seguro de eliminar este producto?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.eliminar(Integer.parseInt(vista.txtId.getText()));
                JOptionPane.showMessageDialog(vista, "Producto eliminado correctamente.");
                limpiar();
                cargarTabla();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(vista, "Error al eliminar: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiar() {
        vista.txtId.setText("");
        vista.txtPrecio.setText("");
        vista.txtDescripcion.setText("");
        vista.txtStock.setText("");
        vista.cmbTipo.setSelectedIndex(0);
        vista.tblProductos.clearSelection();
        // restaura la tabla ccon la busqueda por deescripcion
        if (arbolBase != null) {
            poblarTabla(arbolBase.IND());
        }
    }

    private void cargarTabla() {
        try {
            arbolBase = dao.listar();// retornamos un arbolBusqueda con productos ya insertados

            // recorre el arbol en inorden y arma la lista tipada de productos
            ArrayList<Producto> lista = new ArrayList<>();
            for (Object obj : arbolBase.IND()) {
                lista.add((Producto) obj);
            }

            poblarTabla(lista); // IND = inorden = ordenado por descripción
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar tabla: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void poblarTabla(List<Producto> lista) {
        DefaultTableModel modelo = (DefaultTableModel) vista.tblProductos.getModel();
        modelo.setRowCount(0);
        for (Producto p : lista) {
            modelo.addRow(new Object[] {
                    p.getIdProducto(),
                    p.getPrecio(),
                    p.getTipo(),
                    p.getStock(),
                    p.getDescripcion()
            });
        }
    }

    private void seleccionarFila() {
        int fila = vista.tblProductos.getSelectedRow();
        if (fila >= 0) {
            DefaultTableModel modelo = (DefaultTableModel) vista.tblProductos.getModel();
            vista.txtId.setText(modelo.getValueAt(fila, 0).toString());
            vista.txtPrecio.setText(modelo.getValueAt(fila, 1).toString());
            vista.cmbTipo.setSelectedItem(modelo.getValueAt(fila, 2).toString());
            vista.txtStock.setText(modelo.getValueAt(fila, 3).toString());
            vista.txtDescripcion.setText(modelo.getValueAt(fila, 4).toString());
        }
    }

    private Producto construirDesdeVista() {
        Producto p = new Producto();
        p.setPrecio(new BigDecimal(vista.txtPrecio.getText().trim()));
        p.setDescripcion(vista.txtDescripcion.getText().trim());
        p.setTipo(vista.cmbTipo.getSelectedItem().toString());
        p.setStock(Integer.parseInt(vista.txtStock.getText().trim()));
        return p;
    }

    private boolean validarCampos() {
        if (vista.txtPrecio.getText().trim().isEmpty()
                || vista.txtDescripcion.getText().trim().isEmpty()
                || vista.txtStock.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Precio, Descripción y Stock son obligatorios.",
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            new BigDecimal(vista.txtPrecio.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista,
                    "El Precio debe ser un número válido (ej: 9.99).",
                    "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(vista.txtStock.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista,
                    "El Stock debe ser un número entero válido.",
                    "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}