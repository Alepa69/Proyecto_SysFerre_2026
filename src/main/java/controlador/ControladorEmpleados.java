package controlador;

import com.ues.group.arbolb.ArbolB;
import com.ues.group.vista.VistaEmpleados;
import dao.EmpleadoDAO;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Empleado;

public class ControladorEmpleados {

    private final VistaEmpleados vista;
    private final EmpleadoDAO dao;

    public ControladorEmpleados(VistaEmpleados vista) {
        this.vista = vista;
        this.dao = new EmpleadoDAO();
        iniciarEventos();
        cargarTabla();
    }

    private void iniciarEventos() {
        vista.btnNuevo.addActionListener(e -> nuevo());
        vista.btnGuardar.addActionListener(e -> guardar());
        vista.btnModificar.addActionListener(e -> modificar());
        vista.btnEliminar.addActionListener(e -> eliminar());
        vista.btnLimpiar.addActionListener(e -> limpiar());
        vista.btnBuscar.addActionListener(e -> buscar());
        vista.getBtnBack().addActionListener(e -> vista.dispose());

        vista.tblEmpleados.addMouseListener(new java.awt.event.MouseAdapter() {
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

    private void guardar() {
        if (!validarCampos()) {
            return;
        }
        try {
            dao.insertar(leerFormulario(false));
            JOptionPane.showMessageDialog(vista, "Empleado guardado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            mostrarError("Error al guardar", e);
        }
    }

    private void modificar() {
        if (vista.txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione un empleado de la tabla.");
            return;
        }
        if (!validarCampos()) {
            return;
        }
        try {
            dao.actualizar(leerFormulario(true));
            JOptionPane.showMessageDialog(vista, "Empleado modificado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            mostrarError("Error al modificar", e);
        }
    }

    private void eliminar() {
        if (vista.txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione un empleado de la tabla.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(vista,
                "¿Está seguro de eliminar este empleado?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.eliminar(Integer.parseInt(vista.txtId.getText().trim()));
            JOptionPane.showMessageDialog(vista, "Empleado eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            mostrarError("Error al eliminar", e);
        }
    }

    private void buscar() {
        String id = vista.txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el ID del empleado para buscar.");
            return;
        }
        try {
            Empleado empleado = dao.buscar(Integer.parseInt(id));
            if (empleado == null) {
                JOptionPane.showMessageDialog(vista, "No se encontró un empleado con ese ID.");
                return;
            }
            cargarFormulario(empleado);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "El ID debe ser numérico.",
                    "Dato inválido", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            mostrarError("Error al buscar", e);
        }
    }

    private void cargarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) vista.tblEmpleados.getModel();
        modelo.setRowCount(0);
        try {
            ArbolB<Empleado> empleados = dao.listar();
            for (Empleado empleado : empleados.recorridoEnOrden()) {
                modelo.addRow(new Object[]{
                    empleado.getIdEmpleado(),
                    empleado.getNombre(),
                    empleado.getApellido(),
                    empleado.getTelefono(),
                    empleado.getCorreo(),
                    empleado.getDireccion(),
                    empleado.getNombreUsuario()
                });
            }
        } catch (Exception e) {
            mostrarError("Error al cargar tabla", e);
        }
    }

    private void seleccionarFila() {
        int fila = vista.tblEmpleados.getSelectedRow();
        if (fila < 0) {
            return;
        }
        DefaultTableModel modelo = (DefaultTableModel) vista.tblEmpleados.getModel();
        vista.txtId.setText(valor(modelo.getValueAt(fila, 0)));
        vista.txtNombre.setText(valor(modelo.getValueAt(fila, 1)));
        vista.txtApellido.setText(valor(modelo.getValueAt(fila, 2)));
        vista.txtTelefono.setText(valor(modelo.getValueAt(fila, 3)));
        vista.txtCorreo.setText(valor(modelo.getValueAt(fila, 4)));
        vista.txtDireccion.setText(valor(modelo.getValueAt(fila, 5)));
        vista.txtUsuario.setText(valor(modelo.getValueAt(fila, 6)));

        try {
            Empleado empleado = dao.buscar(Integer.parseInt(vista.txtId.getText()));
            if (empleado != null) {
                vista.txtContrasena.setText(valor(empleado.getContrasena()));
            }
        } catch (Exception e) {
            vista.txtContrasena.setText("");
        }
    }

    private Empleado leerFormulario(boolean incluirId) {
        Empleado empleado = new Empleado();
        if (incluirId) {
            empleado.setIdEmpleado(Integer.parseInt(vista.txtId.getText().trim()));
        }
        empleado.setNombre(vista.txtNombre.getText().trim());
        empleado.setApellido(vista.txtApellido.getText().trim());
        empleado.setTelefono(vista.txtTelefono.getText().trim());
        empleado.setCorreo(vista.txtCorreo.getText().trim());
        empleado.setDireccion(vista.txtDireccion.getText().trim());
        empleado.setNombreUsuario(vista.txtUsuario.getText().trim());
        empleado.setContrasena(new String(vista.txtContrasena.getPassword()).trim());
        return empleado;
    }

    private void cargarFormulario(Empleado empleado) {
        vista.txtId.setText(String.valueOf(empleado.getIdEmpleado()));
        vista.txtNombre.setText(valor(empleado.getNombre()));
        vista.txtApellido.setText(valor(empleado.getApellido()));
        vista.txtTelefono.setText(valor(empleado.getTelefono()));
        vista.txtCorreo.setText(valor(empleado.getCorreo()));
        vista.txtDireccion.setText(valor(empleado.getDireccion()));
        vista.txtUsuario.setText(valor(empleado.getNombreUsuario()));
        vista.txtContrasena.setText(valor(empleado.getContrasena()));
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

    private boolean validarCampos() {
        if (vista.txtNombre.getText().trim().isEmpty()
                || vista.txtApellido.getText().trim().isEmpty()
                || vista.txtUsuario.getText().trim().isEmpty()
                || new String(vista.txtContrasena.getPassword()).trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Nombre, apellido, usuario y contraseña son obligatorios.",
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private String valor(Object valor) {
        return valor == null ? "" : valor.toString();
    }

    private void mostrarError(String titulo, Exception e) {
        JOptionPane.showMessageDialog(vista, titulo + ": " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
