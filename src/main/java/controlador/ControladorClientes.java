
package controlador;

import Arboles.ArbolBusqueda;
import com.ues.group.vista.VistaClientes;
import dao.ClienteDAO;
import modelo.Cliente;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
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

        ButtonGroup grupoOrden = new ButtonGroup();
        grupoOrden.add(vista.rbOrdenAscCliente);
        grupoOrden.add(vista.rbOrdenDescCliente1);

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
        vista.btnOrdenarClientes.addActionListener(e -> ordenarClientes());
        vista.getBtnBack().addActionListener(e -> vista.dispose());

        vista.tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                seleccionarFila();
            }
        });
    }

    private void cargarTabla() {
        try {
            ArbolBusqueda<Cliente> arbol = dao.listar();
            llenarTabla(arbol.IND());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,"Error al cargar tabla: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void llenarTabla(ArrayList<Cliente> lista) {
        DefaultTableModel modelo = (DefaultTableModel) vista.tblClientes.getModel();
        modelo.setRowCount(0);
        for (Cliente c : lista) {
            modelo.addRow(new Object[]{
                c.getIdCliente(),
                c.getNombre(),
                c.getApellido()
            });
        }
    }

    private void nuevo() {
        limpiar();
        vista.txtNombre.requestFocus();
    }

    private void guardar() {
        if (!validarCampos()) return;
        try {
            ArbolBusqueda<Cliente> arbol = dao.listar();
            Cliente criterio = new Cliente();
            criterio.setNombre(vista.txtNombre.getText().trim());
            criterio.setApellido(vista.txtApellido.getText().trim());

            if (arbol.existe(criterio)) {
                JOptionPane.showMessageDialog(vista, "Ya existe un cliente con ese nombre y apellido.", "Duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Cliente c = new Cliente();
            c.setNombre(vista.txtNombre.getText().trim());
            c.setApellido(vista.txtApellido.getText().trim());
            dao.insertar(c);

            JOptionPane.showMessageDialog(vista, "Cliente guardado correctamente.");
            limpiar();
            cargarTabla();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(vista, "Error al modificar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (vista.txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione un cliente de la tabla.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(vista,"¿Esta seguro de eliminar este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.eliminar(Integer.parseInt(vista.txtId.getText()));
                JOptionPane.showMessageDialog(vista, "Cliente eliminado correctamente.");
                limpiar();
                cargarTabla();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(vista, "Error al eliminar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiar() {
        vista.txtId.setText("");
        vista.txtNombre.setText("");
        vista.txtApellido.setText("");
        vista.tblClientes.clearSelection();
    }

    private void buscar() {
        String idTexto = vista.txtId.getText().trim();
        if (idTexto.isEmpty()) {
            String nombre = vista.txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Ingrese un ID o un Nombre para buscar.", "Campo vacío", JOptionPane.WARNING_MESSAGE);
                return;
            }
            buscarPorNombre(nombre);
        } else {
            buscarPorId(idTexto);
        }
    }

    private void buscarPorNombre(String nombre) {
        try {
            ArbolBusqueda<Cliente> arbol = dao.listar();
            Cliente criterio = new Cliente();
            criterio.setNombre(nombre);

            Cliente encontrado = arbol.buscarDato(criterio);

            if (encontrado == null) {
                JOptionPane.showMessageDialog(vista, "No se encontró ningún cliente con nombre: " + nombre, "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            vista.txtId.setText(String.valueOf(encontrado.getIdCliente()));
            vista.txtNombre.setText(encontrado.getNombre());
            vista.txtApellido.setText(encontrado.getApellido());
            resaltarFilaEnTabla(encontrado.getIdCliente());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al buscar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarPorId(String idTexto) {
        try {
            int id = Integer.parseInt(idTexto);
            Cliente c = dao.buscar(id);

            if (c == null) {
                JOptionPane.showMessageDialog(vista, "No se encontró ningún cliente con ID: " + id, "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            vista.txtId.setText(String.valueOf(c.getIdCliente()));
            vista.txtNombre.setText(c.getNombre());
            vista.txtApellido.setText(c.getApellido());
            resaltarFilaEnTabla(c.getIdCliente());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El ID debe ser un número entero.", "ID inválido", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al buscar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ordenarClientes() {
        boolean ascendente  = vista.rbOrdenAscCliente.isSelected();
        boolean descendente = vista.rbOrdenDescCliente1.isSelected();
        String criterio = vista.cbOrdenCliente.getSelectedItem().toString();

        if (!ascendente && !descendente) {
            JOptionPane.showMessageDialog(vista, "Seleccione Ascendente o Descendente.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ArbolBusqueda<Cliente> arbol = dao.listar();
            ArrayList<Cliente> lista = arbol.IND(); 

            switch (criterio) {
                case "ID":
                    lista.sort((a, b) -> Integer.compare(a.getIdCliente(), b.getIdCliente()));
                    break;
                case "APELLIDO":
                    lista.sort((a, b) -> a.getApellido().compareToIgnoreCase(b.getApellido()));
                    break;
                // IND() dwvuelve a-z
            }

            if (descendente) java.util.Collections.reverse(lista);

            llenarTabla(lista);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al ordenar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        String nombre   = vista.txtNombre.getText().trim();
        String apellido = vista.txtApellido.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el nombre.");
            vista.txtNombre.requestFocus();
            return false;
        }
        if (apellido.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el apellido.");
            vista.txtApellido.requestFocus();
            return false;
        }
        return true;
    }

    private void resaltarFilaEnTabla(int idCliente) {
        DefaultTableModel modelo = (DefaultTableModel) vista.tblClientes.getModel();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            if (Integer.parseInt(modelo.getValueAt(i, 0).toString()) == idCliente) {
                vista.tblClientes.setRowSelectionInterval(i, i);
                vista.tblClientes.scrollRectToVisible(
                        vista.tblClientes.getCellRect(i, 0, true));
                break;
            }
        }
    }
}
