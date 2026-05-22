/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import com.ues.group.vista.VistaClientes;
import com.ues.group.vista.VistaMenuPrincipal;
import dao.ClienteDAO;
import modelo.Cliente;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author natha
 */
public class ControladorClientes {

    private final VistaClientes vista;
    private final ClienteDAO dao;
    

    public ControladorClientes(VistaClientes vista) {
        this.vista = vista;
        this.dao = new ClienteDAO();
        iniciarEventos();
        cargarTabla();
        this.vista.getBtnBack().addActionListener(e -> backMenu());
    }

    private void iniciarEventos() {
        vista.btnNuevo.addActionListener(e -> nuevo());
        vista.btnGuardar.addActionListener(e -> guardar());
        vista.btnModificar.addActionListener(e -> modificar());
        vista.btnEliminar.addActionListener(e -> eliminar());
        vista.btnLimpiar.addActionListener(e -> limpiar());
       // vista.btnBack.addActionListener(e -> limpiar());

        vista.tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
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

    private void guardar() {
        if (!validarCampos()) return;
        try {
            Cliente c = new Cliente();
            c.setNombre(vista.txtNombre.getText().trim());
            c.setApellido(vista.txtApellido.getText().trim());
            dao.insertar(c);
            JOptionPane.showMessageDialog(vista, "Cliente guardado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al guardar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificar() {
        if (vista.txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione un cliente de la tabla.");
            return;
        }
        if (!validarCampos()) return;
        try {
            Cliente c = new Cliente();
            c.setIdCliente(Integer.parseInt(vista.txtId.getText()));
            c.setNombre(vista.txtNombre.getText().trim());
            c.setApellido(vista.txtApellido.getText().trim());
            dao.actualizar(c);
            JOptionPane.showMessageDialog(vista, "Cliente modificado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al modificar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (vista.txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione un cliente de la tabla.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(vista,
                "¿Está seguro de eliminar este cliente?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.eliminar(Integer.parseInt(vista.txtId.getText()));
                JOptionPane.showMessageDialog(vista, "Cliente eliminado correctamente.");
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
        vista.tblClientes.clearSelection();
    }

    private void cargarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) vista.tblClientes.getModel();
        modelo.setRowCount(0);
        try {
            List<Cliente> lista = dao.listar();
            for (Cliente c : lista) {
                modelo.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getNombre(),
                    c.getApellido()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar tabla: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarFila() {
        int fila = vista.tblClientes.getSelectedRow();
        if (fila >= 0) {
            DefaultTableModel modelo = (DefaultTableModel) vista.tblClientes.getModel();
            vista.txtId.setText(modelo.getValueAt(fila, 0).toString());
            vista.txtNombre.setText(modelo.getValueAt(fila, 1).toString());
            vista.txtApellido.setText(modelo.getValueAt(fila, 2).toString());
        }
    }

    private boolean validarCampos() {
        if (vista.txtNombre.getText().trim().isEmpty() ||
            vista.txtApellido.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Nombre y Apellido son obligatorios.",
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}
