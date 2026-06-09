
package com.ues.group;

import com.ues.group.vista.VistaLogin;
import controlador.ControladorLogin;

/**
 *
 * @author mendo
 */
public class Proyecto_STD {

    public static void main(String[] args) {
        // Look and feel Nimbus (opcional, deja la app más bonita)
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info
                    : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
        }

        java.awt.EventQueue.invokeLater(() -> {
            VistaLogin login = new VistaLogin();
            new ControladorLogin(login);
            login.setLocationRelativeTo(null);
            login.setVisible(true);
        });
    }
}
