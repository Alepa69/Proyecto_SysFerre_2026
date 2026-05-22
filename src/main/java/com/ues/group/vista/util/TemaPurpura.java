/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.ues.group.vista.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * 
 *
 * @author mendo
 */
public class TemaPurpura {

    public static final Color FONDO_PRINCIPAL = new Color(237, 231, 246); // EDE7F6
    public static final Color FONDO_PANEL    = new Color(243, 239, 250); // F3EFFA
    public static final Color BOTON          = new Color(149, 117, 205); // 9575CD
    public static final Color BOTON_TEXTO    = Color.WHITE;
    public static final Color TITULO_TEXTO   = new Color(74, 20, 140);   // 4A148C
    public static final Color BORDE_TITULO   = new Color(103, 58, 183);  // 673AB7

    
    public static void aplicar(JFrame frame) {
        frame.getContentPane().setBackground(FONDO_PRINCIPAL);
        aplicarRecursivo(frame.getContentPane());
    }

    private static void aplicarRecursivo(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JPanel) {
                c.setBackground(FONDO_PANEL);
                JPanel pnl = (JPanel) c;
                if (pnl.getBorder() instanceof TitledBorder) {
                    TitledBorder tb = (TitledBorder) pnl.getBorder();
                    tb.setTitleColor(BORDE_TITULO);
                    tb.setTitleFont(tb.getTitleFont().deriveFont(Font.BOLD));
                }
            } else if (c instanceof JButton) {
                c.setBackground(BOTON);
                c.setForeground(BOTON_TEXTO);
                ((JButton) c).setFocusPainted(false);
                ((JButton) c).setBorderPainted(false);
                ((JButton) c).setOpaque(true);
            } else if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                String text = lbl.getText();
                if (text != null && esTitulo(text)) {
                    lbl.setForeground(TITULO_TEXTO);
                }
            }
            if (c instanceof Container) {
                aplicarRecursivo((Container) c);
            }
        }
    }

    private static boolean esTitulo(String text) {
        String t = text.toUpperCase();
        return t.startsWith("GESTI") || t.startsWith("INICIO") || t.startsWith("SISTEMA")
                || t.startsWith("REGISTRO") || t.startsWith("INVENTARIO") || t.startsWith("COMPRAS")
                || t.startsWith("REPORTES");
    }
}
