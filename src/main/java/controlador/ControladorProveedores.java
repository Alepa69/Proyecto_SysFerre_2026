package controlador;

import com.ues.group.vista.VistaProveedores;
import Arboles.ArbolBusqueda;
import dao.ProveedoresDAO;
import modelo.Proveedor;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class ControladorProveedores {

    private final VistaProveedores vista;
    private final ProveedoresDAO dao;
    private ArbolBusqueda<Proveedor> arbolBase;
    // arbol cargaddo de la bd, reutilizado parra busqueda y ordenamiento

    public ControladorProveedores(VistaProveedores vista) {
        this.vista = vista;
        this.dao = new ProveedoresDAO();
        configurarComboOrdenar();
        iniciarEventos();
        cargarTabla();
    }

    private void configurarComboOrdenar() {
        vista.cmbOrdenarProveedor.removeAllItems();
        vista.cmbOrdenarProveedor.addItem("ID");
        vista.cmbOrdenarProveedor.addItem("Nombre");
        vista.cmbOrdenarProveedor.addItem("NIT");
    }

    private void iniciarEventos() {
        vista.btnNuevo.addActionListener(e -> nuevo());
        vista.btnGuardar.addActionListener(e -> guardar());
        vista.btnModificar.addActionListener(e -> modificar());
        vista.btnEliminar.addActionListener(e -> eliminar());
        vista.btnLimpiar.addActionListener(e -> limpiar());
        vista.getBtnBack().addActionListener(e -> backMenu());
        vista.btnBuscar.addActionListener(e -> buscar());
        vista.btnOrdenarProveedor.addActionListener(e -> ordenar());

        vista.tblProveedores.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                seleccionarFila();
            }
        });
    }

    private void nuevo() {
        limpiar();
        vista.txtNombre.requestFocus();
    }

    private void backMenu() {
        vista.dispose();
    }

    // busqueda con arbol busqueda
    private void buscar() {
        String texto = vista.txtbuscarIdProveedor.getText().trim();
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
            JOptionPane.showMessageDialog(vista, "No hay proveedores cargados.",
                    "Sin datos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<Proveedor> listaActual = arbolBase.IND();

        // arbol auxiliar para busqueda sencilla por id
        ArbolBusqueda<Integer> arbol = new ArbolBusqueda<>();
        for (Proveedor p : listaActual) {
            arbol.insertar(p.getIdProveedor());
        }

        if (arbol.buscar(idBuscado) == null) {
            JOptionPane.showMessageDialog(vista, "No se encontró proveedor con ID " +
                    idBuscado + ".",
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Proveedor resultado = listaActual.stream()
                .filter(p -> p.getIdProveedor() == idBuscado)
                .findFirst()
                .orElse(null);

        if (resultado != null) {
            DefaultTableModel modelo = (DefaultTableModel) vista.tblProveedores.getModel();
            modelo.setRowCount(0);
            modelo.addRow(new Object[] {
                    resultado.getIdProveedor(),
                    resultado.getNombre(),
                    resultado.getNit(),
                    resultado.getDireccion(),
                    resultado.getTelefonos(),
                    resultado.getCorreo()
            });
            vista.txtId.setText(String.valueOf(resultado.getIdProveedor()));
            vista.txtNombre.setText(resultado.getNombre());
            vista.txtNit.setText(resultado.getNit());
            vista.txtDireccion.setText(resultado.getDireccion());
            vista.txtTelefonos.setText(resultado.getTelefonos());
            vista.txtCorreo.setText(resultado.getCorreo());
        }
    }

    // ordenamos con arbol de busqueda
    private void ordenar() {
        if (arbolBase == null || arbolBase.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No hay proveedores para ordenar.",
                    "Sin datos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String criterio = vista.cmbOrdenarProveedor.getSelectedItem().toString();
        List<Proveedor> ordenados;

        // segun el criterio construimos un arbol distinto con el campo a ordenar
        // el inorden de cada arbol nos da la lista ya ordenada por ese campo
        switch (criterio) {
            case "ID":
                ordenados = ordenarPorId();
                break;
            case "Nombre":
                ordenados = ordenarPorNombre();
                break;
            case "NIT":
                ordenados = ordenarPorNit();
                break;
            default:
                ordenados = arbolBase.IND();
        }

        poblarTabla(ordenados);
    }

    @SuppressWarnings("unchecked")
    private List<Proveedor> ordenarPorId() {
        // insertamos solo los id en un arbol nuevo
        // como es ind obtenemos los id en orden ascendente
        ArbolBusqueda<Integer> arbol = new ArbolBusqueda<>();
        List<Proveedor> listaActual = arbolBase.IND();
        for (Proveedor p : listaActual) {
            arbol.insertar(p.getIdProveedor());
        }

        // ind para simplificar respetando el orden que dio el arbol
        List<Integer> idsOrdenados = arbol.IND();

        List<Proveedor> copiaActual = new ArrayList<>(listaActual);
        List<Proveedor> resultado = new ArrayList<>();

        for (Integer id : idsOrdenados) {
            copiaActual.stream()
                    .filter(p -> p.getIdProveedor() == id)
                    .findFirst()
                    .ifPresent(p -> {
                        resultado.add(p);
                        copiaActual.remove(p);
                    });
        }
        return resultado;
    }

    @SuppressWarnings("unchecked")
    private List<Proveedor> ordenarPorNombre() {
        List<Proveedor> listaActual = arbolBase.IND();

        ArbolBusqueda<String> arbol = new ArbolBusqueda<>();
        for (Proveedor p : listaActual) {
            arbol.insertar(p.getNombre().toLowerCase());
        }
        List<String> nombresOrdenados = arbol.IND();

        List<Proveedor> copiaActual = new ArrayList<>(listaActual);
        List<Proveedor> resultado = new ArrayList<>();
        for (String nombre : nombresOrdenados) {
            copiaActual.stream()
                    // usamos tolowecaser para que la comparacion no distinga mayus de minus
                    .filter(p -> p.getNombre().toLowerCase().equals(nombre))
                    .findFirst()
                    .ifPresent(p -> {
                        resultado.add(p);
                        copiaActual.remove(p);
                    });
        }
        return resultado;
    }

    @SuppressWarnings("unchecked")
    private List<Proveedor> ordenarPorNit() {
        List<Proveedor> listaActual = arbolBase.IND();

        ArbolBusqueda<String> arbol = new ArbolBusqueda<>();
        for (Proveedor p : listaActual) {
            arbol.insertar(p.getNit().toLowerCase());
        }
        List<String> nitsOrdenados = arbol.IND();

        List<Proveedor> copiaActual = new ArrayList<>(listaActual);
        List<Proveedor> resultado = new ArrayList<>();
        for (String nit : nitsOrdenados) {
            copiaActual.stream()
                    // implementamos de la misma manera arbol de string para ordenar alfabeticamente
                    .filter(p -> p.getNit().toLowerCase().equals(nit))
                    .findFirst()
                    .ifPresent(p -> {
                        resultado.add(p);
                        copiaActual.remove(p);
                    });
        }
        return resultado;
    }

    // crud
    private void guardar() {
        if (!validarCampos())
            return;
        try {
            Proveedor p = construirDesdeVista();
            dao.insertar(p);
            JOptionPane.showMessageDialog(vista, "Proveedor guardado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al guardar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificar() {
        if (vista.txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione un proveedor de la tabla.");
            return;
        }
        if (!validarCampos())
            return;
        try {
            Proveedor p = construirDesdeVista();
            p.setIdProveedor(Integer.parseInt(vista.txtId.getText()));
            dao.actualizar(p);
            JOptionPane.showMessageDialog(vista, "Proveedor modificado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al modificar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (vista.txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione un proveedor de la tabla.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(vista,
                "¿Está seguro de eliminar este proveedor?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.eliminar(Integer.parseInt(vista.txtId.getText()));
                JOptionPane.showMessageDialog(vista, "Proveedor eliminado correctamente.");
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
        vista.txtNombre.setText("");
        vista.txtNit.setText("");
        vista.txtDireccion.setText("");
        vista.txtTelefonos.setText("");
        vista.txtCorreo.setText("");
        vista.txtbuscarIdProveedor.setText("");
        vista.tblProveedores.clearSelection();
        if (arbolBase != null) {
            poblarTabla(arbolBase.IND());
        }
    }

    private void cargarTabla() {
        try {
            arbolBase = dao.listar();
            poblarTabla(arbolBase.IND());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar tabla: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void poblarTabla(List<Proveedor> lista) {
        DefaultTableModel modelo = (DefaultTableModel) vista.tblProveedores.getModel();
        modelo.setRowCount(0);
        for (Proveedor p : lista) {
            modelo.addRow(new Object[] {
                    p.getIdProveedor(),
                    p.getNombre(),
                    p.getNit(),
                    p.getDireccion(),
                    p.getTelefonos(),
                    p.getCorreo()
            });
        }
    }

    private void seleccionarFila() {
        int fila = vista.tblProveedores.getSelectedRow();
        if (fila >= 0) {
            DefaultTableModel modelo = (DefaultTableModel) vista.tblProveedores.getModel();
            vista.txtId.setText(modelo.getValueAt(fila, 0).toString());
            vista.txtNombre.setText(modelo.getValueAt(fila, 1).toString());
            vista.txtNit.setText(modelo.getValueAt(fila, 2).toString());
            vista.txtDireccion.setText(modelo.getValueAt(fila, 3).toString());
            vista.txtTelefonos.setText(modelo.getValueAt(fila, 4).toString());
            vista.txtCorreo.setText(modelo.getValueAt(fila, 5).toString());
        }
    }

    private Proveedor construirDesdeVista() {
        Proveedor p = new Proveedor();
        p.setNombre(vista.txtNombre.getText().trim());
        p.setNit(vista.txtNit.getText().trim());
        p.setDireccion(vista.txtDireccion.getText().trim());
        p.setTelefonos(vista.txtTelefonos.getText().trim());
        p.setCorreo(vista.txtCorreo.getText().trim());
        return p;
    }

    private boolean validarCampos() {
        if (vista.txtNombre.getText().trim().isEmpty()
                || vista.txtNit.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Nombre y NIT son obligatorios.",
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}