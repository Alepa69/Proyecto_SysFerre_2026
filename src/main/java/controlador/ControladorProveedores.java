// package controlador;

// import com.ues.group.vista.VistaProveedores;

// import Arboles.ArbolBinario;
// import dao.ProveedoresDAO;
// import modelo.Proveedor;
// import javax.swing.table.DefaultTableModel;
// import java.util.List;
// import javax.swing.JOptionPane;

// public class ControladorProveedores {

// private final VistaProveedores vista;
// private final ProveedoresDAO dao;

// // Lista base cargada desde BD, usada para árbol B
// private List<Proveedor> listaBase;

// public ControladorProveedores(VistaProveedores vista) {
// this.vista = vista;
// this.dao = new ProveedoresDAO();
// configurarComboOrdenar();
// iniciarEventos();
// cargarTabla();
// }

// // Rellena el combo con las opciones de ordenamiento
// private void configurarComboOrdenar() {
// vista.cmbOrdenarProveedor.removeAllItems();
// vista.cmbOrdenarProveedor.addItem("ID");
// vista.cmbOrdenarProveedor.addItem("Nombre");
// vista.cmbOrdenarProveedor.addItem("NIT");
// }

// private void iniciarEventos() {
// vista.btnNuevo.addActionListener(e -> nuevo());
// vista.btnGuardar.addActionListener(e -> guardar());
// vista.btnModificar.addActionListener(e -> modificar());
// vista.btnEliminar.addActionListener(e -> eliminar());
// vista.btnLimpiar.addActionListener(e -> limpiar());
// vista.getBtnBack().addActionListener(e -> backMenu());
// vista.btnBuscar.addActionListener(e -> buscar());
// vista.btnOrdenarProveedor.addActionListener(e -> ordenar());

// vista.tblProveedores.addMouseListener(new java.awt.event.MouseAdapter() {
// @Override
// public void mouseClicked(java.awt.event.MouseEvent e) {
// seleccionarFila();
// }
// });
// }

// private void nuevo() {
// limpiar();
// vista.txtNombre.requestFocus();
// }

// private void backMenu() {
// vista.dispose();
// }

// // ─── BÚSQUEDA CON ÁRBOL B (por ID) ───────────────────────────────────────
// private void buscar() {
// String texto = vista.txtbuscarIdProveedor.getText().trim();
// if (texto.isEmpty()) {
// JOptionPane.showMessageDialog(vista, "Ingrese un ID para buscar.",
// "Campo vacío", JOptionPane.WARNING_MESSAGE);
// return;
// }
// int idBuscado;
// try {
// idBuscado = Integer.parseInt(texto);
// } catch (NumberFormatException e) {
// JOptionPane.showMessageDialog(vista, "El ID debe ser un número entero.",
// "Formato inválido", JOptionPane.WARNING_MESSAGE);
// return;
// }

// if (listaBase == null || listaBase.isEmpty()) {
// JOptionPane.showMessageDialog(vista, "No hay proveedores cargados.",
// "Sin datos", JOptionPane.INFORMATION_MESSAGE);
// return;
// }

// // Construir árbol B con los IDs y buscar
// ArbolBinario<Integer> arbol = new ArbolBinario<>();
// for (Proveedor p : listaBase) {
// arbol.insertar(p.getIdProveedor());
// }

// Integer encontrado = arbol.buscar(idBuscado);
// if (encontrado == null) {
// JOptionPane.showMessageDialog(vista, "No se encontró proveedor con ID " +
// idBuscado + ".",
// "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
// return;
// }

// // Encontrado en árbol → buscar objeto completo en la lista
// Proveedor resultado = listaBase.stream()
// .filter(p -> p.getIdProveedor() == idBuscado)
// .findFirst()
// .orElse(null);

// if (resultado != null) {
// // Mostrar solo ese registro en la tabla
// DefaultTableModel modelo = (DefaultTableModel)
// vista.tblProveedores.getModel();
// modelo.setRowCount(0);
// modelo.addRow(new Object[] {
// resultado.getIdProveedor(),
// resultado.getNombre(),
// resultado.getNit(),
// resultado.getDireccion(),
// resultado.getTelefonos(),
// resultado.getCorreo()
// });
// // Rellenar formulario
// vista.txtId.setText(String.valueOf(resultado.getIdProveedor()));
// vista.txtNombre.setText(resultado.getNombre());
// vista.txtNit.setText(resultado.getNit());
// vista.txtDireccion.setText(resultado.getDireccion());
// vista.txtTelefonos.setText(resultado.getTelefonos());
// vista.txtCorreo.setText(resultado.getCorreo());
// }
// }

// // ─── ORDENAMIENTO CON ÁRBOL B ─────────────────────────────────────────────
// private void ordenar() {
// if (listaBase == null || listaBase.isEmpty()) {
// JOptionPane.showMessageDialog(vista, "No hay proveedores para ordenar.",
// "Sin datos", JOptionPane.INFORMATION_MESSAGE);
// return;
// }

// String criterio = vista.cmbOrdenarProveedor.getSelectedItem().toString();
// List<Proveedor> ordenados;

// switch (criterio) {
// case "ID":
// ordenados = ordenarPorId();
// break;
// case "Nombre":
// ordenados = ordenarPorNombre();
// break;
// case "NIT":
// ordenados = ordenarPorNit();
// break;
// default:
// ordenados = listaBase;
// }

// poblarTabla(ordenados);
// }

// // Ordena por ID usando árbol B de enteros
// private List<Proveedor> ordenarPorId() {
// ArbolBinario<Integer> arbol = new ArbolBinario<>();
// for (Proveedor p : listaBase) {
// arbol.insertar(p.getIdProveedor());
// }
// List<Integer> idsOrdenados = arbol.recorridoEnOrden();

// List<Proveedor> resultado = new java.util.ArrayList<>();
// for (Integer id : idsOrdenados) {
// listaBase.stream()
// .filter(p -> p.getIdProveedor() == id)
// .findFirst()
// .ifPresent(resultado::add);
// }
// return resultado;
// }

// // Ordena por Nombre usando árbol B de Strings
// private List<Proveedor> ordenarPorNombre() {
// ArbolBinario<String> arbol = new ArbolBinario<>();
// for (Proveedor p : listaBase) {
// arbol.insertar(p.getNombre().toLowerCase());
// }
// List<String> nombresOrdenados = arbol.recorridoEnOrden();

// List<Proveedor> resultado = new java.util.ArrayList<>();
// for (String nombre : nombresOrdenados) {
// listaBase.stream()
// .filter(p -> p.getNombre().toLowerCase().equals(nombre))
// .findFirst()
// .ifPresent(resultado::add);
// }
// return resultado;
// }

// // Ordena por NIT usando árbol B de Strings
// private List<Proveedor> ordenarPorNit() {
// ArbolBinario<String> arbol = new ArbolBinario<>();
// for (Proveedor p : listaBase) {
// arbol.insertar(p.getNit().toLowerCase());
// }
// List<String> nitsOrdenados = arbol.recorridoEnOrden();

// List<Proveedor> resultado = new java.util.ArrayList<>();
// for (String nit : nitsOrdenados) {
// listaBase.stream()
// .filter(p -> p.getNit().toLowerCase().equals(nit))
// .findFirst()
// .ifPresent(resultado::add);
// }
// return resultado;
// }

// // ─── CRUD ─────────────────────────────────────────────────────────────────
// private void guardar() {
// if (!validarCampos())
// return;
// try {
// Proveedor p = construirDesdeVista();
// dao.insertar(p);
// JOptionPane.showMessageDialog(vista, "Proveedor guardado correctamente.");
// limpiar();
// cargarTabla();
// } catch (Exception e) {
// JOptionPane.showMessageDialog(vista, "Error al guardar: " + e.getMessage(),
// "Error", JOptionPane.ERROR_MESSAGE);
// }
// }

// private void modificar() {
// if (vista.txtId.getText().isEmpty()) {
// JOptionPane.showMessageDialog(vista, "Seleccione un proveedor de la tabla.");
// return;
// }
// if (!validarCampos())
// return;
// try {
// Proveedor p = construirDesdeVista();
// p.setIdProveedor(Integer.parseInt(vista.txtId.getText()));
// dao.actualizar(p);
// JOptionPane.showMessageDialog(vista, "Proveedor modificado correctamente.");
// limpiar();
// cargarTabla();
// } catch (Exception e) {
// JOptionPane.showMessageDialog(vista, "Error al modificar: " + e.getMessage(),
// "Error", JOptionPane.ERROR_MESSAGE);
// }
// }

// private void eliminar() {
// if (vista.txtId.getText().isEmpty()) {
// JOptionPane.showMessageDialog(vista, "Seleccione un proveedor de la tabla.");
// return;
// }
// int confirm = JOptionPane.showConfirmDialog(vista,
// "¿Está seguro de eliminar este proveedor?",
// "Confirmar", JOptionPane.YES_NO_OPTION);
// if (confirm == JOptionPane.YES_OPTION) {
// try {
// dao.eliminar(Integer.parseInt(vista.txtId.getText()));
// JOptionPane.showMessageDialog(vista, "Proveedor eliminado correctamente.");
// limpiar();
// cargarTabla();
// } catch (Exception e) {
// JOptionPane.showMessageDialog(vista, "Error al eliminar: " + e.getMessage(),
// "Error", JOptionPane.ERROR_MESSAGE);
// }
// }
// }

// private void limpiar() {
// vista.txtId.setText("");
// vista.txtNombre.setText("");
// vista.txtNit.setText("");
// vista.txtDireccion.setText("");
// vista.txtTelefonos.setText("");
// vista.txtCorreo.setText("");
// vista.txtbuscarIdProveedor.setText("");
// vista.tblProveedores.clearSelection();
// // Restaurar tabla completa si se había filtrado por búsqueda
// if (listaBase != null) {
// poblarTabla(listaBase);
// }
// }

// private void cargarTabla() {
// try {
// listaBase = dao.listar();
// poblarTabla(listaBase);
// } catch (Exception e) {
// JOptionPane.showMessageDialog(vista, "Error al cargar tabla: " +
// e.getMessage(),
// "Error", JOptionPane.ERROR_MESSAGE);
// }
// }

// private void poblarTabla(List<Proveedor> lista) {
// DefaultTableModel modelo = (DefaultTableModel)
// vista.tblProveedores.getModel();
// modelo.setRowCount(0);
// for (Proveedor p : lista) {
// modelo.addRow(new Object[] {
// p.getIdProveedor(),
// p.getNombre(),
// p.getNit(),
// p.getDireccion(),
// p.getTelefonos(),
// p.getCorreo()
// });
// }
// }

// private void seleccionarFila() {
// int fila = vista.tblProveedores.getSelectedRow();
// if (fila >= 0) {
// DefaultTableModel modelo = (DefaultTableModel)
// vista.tblProveedores.getModel();
// vista.txtId.setText(modelo.getValueAt(fila, 0).toString());
// vista.txtNombre.setText(modelo.getValueAt(fila, 1).toString());
// vista.txtNit.setText(modelo.getValueAt(fila, 2).toString());
// vista.txtDireccion.setText(modelo.getValueAt(fila, 3).toString());
// vista.txtTelefonos.setText(modelo.getValueAt(fila, 4).toString());
// vista.txtCorreo.setText(modelo.getValueAt(fila, 5).toString());
// }
// }

// private Proveedor construirDesdeVista() {
// Proveedor p = new Proveedor();
// p.setNombre(vista.txtNombre.getText().trim());
// p.setNit(vista.txtNit.getText().trim());
// p.setDireccion(vista.txtDireccion.getText().trim());
// p.setTelefonos(vista.txtTelefonos.getText().trim());
// p.setCorreo(vista.txtCorreo.getText().trim());
// return p;
// }

// private boolean validarCampos() {
// if (vista.txtNombre.getText().trim().isEmpty()
// || vista.txtNit.getText().trim().isEmpty()) {
// JOptionPane.showMessageDialog(vista, "Nombre y NIT son obligatorios.",
// "Campos vacíos", JOptionPane.WARNING_MESSAGE);
// return false;
// }
// return true;
// }
// }