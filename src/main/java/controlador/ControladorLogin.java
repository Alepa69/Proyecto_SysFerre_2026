/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.ues.group.vista.VistaLogin;
import com.ues.group.vista.VistaMenuPrincipal;
import javax.swing.JOptionPane;

/**
 *
 * @author mendo
 */
public class ControladorLogin {

    private final VistaLogin vista;

    private static final String USUARIO_TMP = "admin";
    private static final String CLAVE_TMP = "admin";

    public ControladorLogin(VistaLogin vista) {
        this.vista = vista;
        iniciarEventos();
    }

    private void iniciarEventos() {
        vista.btnIngresar.addActionListener(e -> ingresar());
        vista.btnCancelar.addActionListener(e -> cancelar());

        vista.txtContrasena.addActionListener(e -> ingresar());
    }

    private void ingresar() {
        String usuario = vista.txtUsuario.getText().trim();
        String clave = new String(vista.txtContrasena.getPassword());

        if (usuario.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Debe ingresar usuario y contraseña.",
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (USUARIO_TMP.equals(usuario) && CLAVE_TMP.equals(clave)) {
            abrirMenu();
        } else {
            JOptionPane.showMessageDialog(vista,
                    "Usuario o contraseña incorrectos.",
                    "Acceso denegado", JOptionPane.ERROR_MESSAGE);
            vista.txtContrasena.setText("");
            vista.txtUsuario.requestFocus();
        }
    }

    private void abrirMenu() {
        VistaMenuPrincipal menu = new VistaMenuPrincipal();
        new ControladorMenu(menu);
        menu.setLocationRelativeTo(null);
        menu.setVisible(true);
        vista.dispose();
    }

    private void cancelar() {
        System.exit(0);
    }
}
