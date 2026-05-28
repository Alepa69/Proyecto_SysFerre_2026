/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.ues.group.vista.VistaLogin;
import com.ues.group.vista.VistaMenuPrincipal;
import javax.swing.JOptionPane;
import modelo.Usuario;

/**
 *
 * @author mendo
 */
public class ControladorLogin {

    private final VistaLogin loginVista;
    private VistaMenuPrincipal vista;
    private final Usuario loginModelo;

    public ControladorLogin(VistaLogin loginVista) {
        this.loginVista = loginVista;
        this.loginModelo = new Usuario();
        this.vista = null;

        // Para botón Enter
        this.loginVista.getRootPane().setDefaultButton(this.loginVista.btnIngresar);
        this.loginVista.btnIngresar.addActionListener(e -> login());
        this.loginVista.btnCancelar.addActionListener(e -> System.exit(0));
    }

    public void iniciar() {
        loginVista.setLocationRelativeTo(null);
        loginVista.setVisible(true);
    }

    private void login() {
        String usuario = loginVista.txtUsuario.getText().trim();
        String contrasena = new String(loginVista.txtContrasena.getPassword()).trim();

        // Validar que los campos no estén vacíos
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Por favor ingrese usuario y contraseña.");
            return;
        }

        // Validar credenciales contra el modelo
        if (loginModelo.validarCredenciales(usuario, contrasena)) {
            cerrar();
            vista = new VistaMenuPrincipal();
            new ControladorMenu(vista);
            vista.setLocationRelativeTo(null);
            vista.setVisible(true);
        } else {
            mostrarError("Usuario o contraseña incorrectos.");
            loginVista.txtContrasena.setText("");
            loginVista.txtContrasena.requestFocus();
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(loginVista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void cerrar() {
        loginVista.dispose();
    }

    public void cerrarSesion() {
        if (vista != null) {
            vista.dispose();
            vista = null;
        }
        loginVista.txtUsuario.setText("");
        loginVista.txtContrasena.setText("");
        loginVista.txtUsuario.requestFocus();
        loginVista.setLocationRelativeTo(null);
        loginVista.setVisible(true);
    }
}