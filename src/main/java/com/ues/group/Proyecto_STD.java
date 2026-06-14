
package com.ues.group;

import com.ues.group.vista.VistaLogin;
import com.ues.group.vista.VistaMenuPrincipal;
import com.ues.group.vista.VistaProveedores;

import controlador.ControladorLogin;
import controlador.ControladorMenu;
import controlador.ControladorProveedores;

/**
 *
 * @author mendo
 */
public class Proyecto_STD {

    public static void main(String[] args) {
        // Look and feel Nimbus (opcional, deja la app más bonita)
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
        }

        // java.awt.EventQueue.invokeLater(() -> {
        // VistaLogin login = new VistaLogin();
        // new ControladorLogin(login);
        // login.setLocationRelativeTo(null);
        // login.setVisible(true);
        // });

        /* PARA PROBAR VISTAS INDIVIDUALES */
        java.awt.EventQueue.invokeLater(() -> {
            VistaMenuPrincipal login = new VistaMenuPrincipal();
            new ControladorMenu(login);
            login.setLocationRelativeTo(null);
            login.setVisible(true);
        });
    }
}
