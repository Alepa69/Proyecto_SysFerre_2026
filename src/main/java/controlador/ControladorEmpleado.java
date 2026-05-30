
package controlador;

import com.ues.group.vista.VistaEmpleados;

import Arboles.ArbolBinario;
import dao.EmpleadoDAO;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Empleado;
import modelo.Usuario;

/*
 Controlador de Empleados con Árbol B integrado.
 */
public class ControladorEmpleado {

    private static final int ORDEN_ARBOL = 3;

    private final VistaEmpleados vista;
    private final EmpleadoDAO dao;

    // Árbol B en memoria – se reconstruye cada vez que cambian los datos
    private ArbolBinario<NodoEmpleado> arbol;

    public ControladorEmpleado(VistaEmpleados vista) {
        this.vista = vista;
        this.dao = new EmpleadoDAO();
        iniciarEventos();
        cargarTabla();
    }

    // ── Eventos ──────────────────────────────────────────────────────────────

    private void iniciarEventos() {
        vista.btnNuevo.addActionListener(e -> nuevo());
        vista.btnGuardar.addActionListener(e -> guardar());
        vista.btnModificar.addActionListener(e -> modificar());
        vista.btnEliminar.addActionListener(e -> eliminar());
        vista.btnLimpiar.addActionListener(e -> limpiar());
        vista.btnBuscar.addActionListener(e -> buscar());

        vista.getBtnBack1().addActionListener(e -> vista.dispose());

        vista.tblEmpleados.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                seleccionarFila();
            }
        });
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    private void nuevo() {
        limpiar();
        vista.txtNombre.requestFocus();
    }

    private void guardar() {
        if (!validarCampos())
            return;
        try {
            Empleado emp = construirDesdeVista();
            String usuario = vista.txtUsuario.getText().trim();
            String contrasena = new String(vista.txtContrasena.getPassword()).trim();
            dao.insertar(emp, usuario, contrasena);
            JOptionPane.showMessageDialog(vista, "Empleado guardado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al guardar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificar() {
        if (vista.txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione un empleado de la tabla.");
            return;
        }
        if (!validarCampos())
            return;
        try {
            Empleado emp = construirDesdeVista();
            emp.setIdEmpleado(Integer.parseInt(vista.txtId.getText().trim()));
            String usuario = vista.txtUsuario.getText().trim();
            String contrasena = new String(vista.txtContrasena.getPassword()).trim();
            dao.actualizar(emp, usuario, contrasena);
            JOptionPane.showMessageDialog(vista, "Empleado modificado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al modificar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (vista.txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione un empleado de la tabla.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(vista,
                "¿Está seguro de eliminar este empleado?\n"
                        + "También se eliminará su usuario de acceso.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.eliminar(Integer.parseInt(vista.txtId.getText().trim()));
                JOptionPane.showMessageDialog(vista, "Empleado eliminado correctamente.");
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
        vista.txtApellido.setText("");
        vista.txtTelefono.setText("");
        vista.txtCorreo.setText("");
        vista.txtDireccion.setText("");
        vista.txtUsuario.setText("");
        vista.txtContrasena.setText("");
        vista.tblEmpleados.clearSelection();
    }

    // ── Árbol B – búsqueda ────────────────────────────────────────────────────

    /**
     * Busca un empleado por ID usando el árbol B en memoria.
     * Rellena el formulario con sus datos y carga sus credenciales de usuario.
     */
    private void buscar() {
        String texto = vista.txtId.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el ID del empleado a buscar.");
            return;
        }
        try {
            int id = Integer.parseInt(texto);

            if (arbol == null || arbol.estaVacio()) {
                reconstruirArbol();
            }

            // Nodo clave de búsqueda (solo necesita el id)
            Empleado clave = new Empleado();
            clave.setIdEmpleado(id);
            NodoEmpleado resultado = arbol.buscar(new NodoEmpleado(clave));

            if (resultado == null) {
                JOptionPane.showMessageDialog(vista,
                        "No se encontró ningún empleado con ID " + id + ".");
                return;
            }

            Empleado emp = resultado.empleado;
            vista.txtNombre.setText(emp.getNombre());
            vista.txtApellido.setText(emp.getApellido());
            vista.txtTelefono.setText(nullSafe(emp.getTelefono()));
            vista.txtCorreo.setText(nullSafe(emp.getCorreo()));
            vista.txtDireccion.setText(nullSafe(emp.getDireccion()));

            // Cargar credenciales de usuario
            Usuario usr = dao.buscarUsuarioPorEmpleado(id);
            if (usr != null) {
                vista.txtUsuario.setText(usr.getNombreUsuario());
                vista.txtContrasena.setText(usr.getContrasena());
            } else {
                vista.txtUsuario.setText("");
                vista.txtContrasena.setText("");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El ID debe ser un número entero.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al buscar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Árbol B – carga de tabla ──────────────────────────────────────────────

    /**
     * Carga todos los empleados desde la BD, los inserta en el árbol B
     * y pinta la tabla en orden por ID (recorrido en orden del árbol).
     */
    private void cargarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) vista.tblEmpleados.getModel();
        modelo.setRowCount(0);
        try {
            List<Empleado> lista = dao.listar();
            reconstruirArbol(lista);

            for (NodoEmpleado nodo : arbol.recorridoEnOrden()) {
                Empleado emp = nodo.empleado;
                String usrNombre = (emp.getUsuario() != null)
                        ? emp.getUsuario().getNombreUsuario()
                        : "";
                modelo.addRow(new Object[] {
                        emp.getIdEmpleado(),
                        emp.getNombre(),
                        emp.getApellido(),
                        emp.getTelefono(),
                        emp.getCorreo(),
                        emp.getDireccion(),
                        usrNombre
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar tabla: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reconstruirArbol() {
        try {
            reconstruirArbol(dao.listar());
        } catch (Exception e) {
            arbol = new ArbolB<>(ORDEN_ARBOL);
        }
    }

    private void reconstruirArbol(List<Empleado> lista) {
        arbol = new ArbolB<>(ORDEN_ARBOL);
        for (Empleado emp : lista) {
            arbol.insertar(new NodoEmpleado(emp));
        }
    }

    // ── Tabla (clic) ──────────────────────────────────────────────────────────

    private void seleccionarFila() {
        int fila = vista.tblEmpleados.getSelectedRow();
        if (fila >= 0) {
            DefaultTableModel modelo = (DefaultTableModel) vista.tblEmpleados.getModel();
            vista.txtId.setText(modelo.getValueAt(fila, 0).toString());
            vista.txtNombre.setText(modelo.getValueAt(fila, 1).toString());
            vista.txtApellido.setText(modelo.getValueAt(fila, 2).toString());
            vista.txtTelefono.setText(nullSafe(modelo.getValueAt(fila, 3)));
            vista.txtCorreo.setText(nullSafe(modelo.getValueAt(fila, 4)));
            vista.txtDireccion.setText(nullSafe(modelo.getValueAt(fila, 5)));
            vista.txtUsuario.setText(nullSafe(modelo.getValueAt(fila, 6)));
            vista.txtContrasena.setText("");

            // Cargar contraseña real del servidor
            try {
                int id = Integer.parseInt(vista.txtId.getText().trim());
                Usuario usr = dao.buscarUsuarioPorEmpleado(id);
                if (usr != null) {
                    vista.txtContrasena.setText(usr.getContrasena());
                }
            } catch (Exception ignored) {
            }
        }
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private Empleado construirDesdeVista() {
        Empleado emp = new Empleado();
        emp.setNombre(vista.txtNombre.getText().trim());
        emp.setApellido(vista.txtApellido.getText().trim());
        emp.setTelefono(vista.txtTelefono.getText().trim());
        emp.setCorreo(vista.txtCorreo.getText().trim());
        emp.setDireccion(vista.txtDireccion.getText().trim());
        return emp;
    }

    private boolean validarCampos() {
        if (vista.txtNombre.getText().trim().isEmpty()
                || vista.txtApellido.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Nombre y Apellido son obligatorios.",
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        String usr = vista.txtUsuario.getText().trim();
        String pwd = new String(vista.txtContrasena.getPassword()).trim();
        if ((!usr.isEmpty() && pwd.isEmpty()) || (usr.isEmpty() && !pwd.isEmpty())) {
            JOptionPane.showMessageDialog(vista,
                    "Si desea crear usuario, complete tanto Usuario como Contraseña.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private String nullSafe(Object value) {
        return value != null ? value.toString() : "";
    }

    // ── Nodo Árbol B ─────────────────────────────────────────────────────────

    /**
     * Nodo que envuelve un Empleado para el ArbolB.
     * La comparación se hace por idEmpleado (clave primaria).
     */
    private static class NodoEmpleado implements Comparable<NodoEmpleado> {

        final Empleado empleado;

        NodoEmpleado(Empleado empleado) {
            this.empleado = empleado;
        }

        @Override
        public int compareTo(NodoEmpleado otro) {
            return Integer.compare(
                    this.empleado.getIdEmpleado(),
                    otro.empleado.getIdEmpleado());
        }
    }
}
