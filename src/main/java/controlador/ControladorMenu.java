/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.ues.group.vista.VistaClientes;
import com.ues.group.vista.VistaEmpleados;
import com.ues.group.vista.VistaInventario;
import com.ues.group.vista.VistaMenuPrincipal;
import com.ues.group.vista.VistaProductos;
import com.ues.group.vista.VistaProveedores;
import com.ues.group.vista.VistaReabastecimiento;
import com.ues.group.vista.VistaVentas;

/**
 * @author alexi
 */
public class ControladorMenu {
    private VistaMenuPrincipal vista;

    public ControladorMenu() {
    }

    public ControladorMenu(VistaMenuPrincipal vista) {
        this.vista = vista;

        // Eventos
        this.vista.getBtnClientes().addActionListener(e -> llamarClientes());
        this.vista.getBtnCompras().addActionListener(e -> llamarCompras());
        this.vista.getBtnEmpleados().addActionListener(e -> llamarEmpleados());
        this.vista.getBtnInventario().addActionListener(e -> llamarInventario());
        this.vista.getBtnProductos().addActionListener(e -> llamarProductos());
        this.vista.getBtnProveedores().addActionListener(e -> llamarProveedores());
        this.vista.getBtnVentas().addActionListener(e -> llamarVentas());
        this.vista.getBtnSalir().addActionListener(e -> salir());
    }

    // Métodos para llamar a cada submenu
    // Métodos para llamar a cada submenu
    private void llamarClientes() {
        VistaClientes vista = new VistaClientes();
        ControladorClientes control = new ControladorClientes(vista);
        vista.setVisible(true);
    }

    private void llamarCompras() {
        VistaReabastecimiento vista = new VistaReabastecimiento();
        ControladorReabastecimiento control = new ControladorReabastecimiento(vista);
        vista.setVisible(true);
    }

    private void llamarEmpleados() {
        VistaEmpleados vista = new VistaEmpleados();
        ControladorEmpleado control = new ControladorEmpleado(vista);
        vista.setVisible(true);
    }

    private void llamarInventario() {
        VistaInventario vista = new VistaInventario();
        ControladorInventario control = new ControladorInventario(vista);
        vista.setVisible(true);
    }

    private void llamarProductos() {
        VistaProductos vista = new VistaProductos();
        ControladorProducto control = new ControladorProducto(vista);
        vista.setVisible(true);
    }

    private void llamarProveedores() {
        VistaProveedores vista = new VistaProveedores();
        ControladorProveedores control = new ControladorProveedores(vista);
        vista.setVisible(true);
    }


    private void llamarVentas() {
        VistaVentas vista = new VistaVentas();
        ControladorVentas control = new ControladorVentas(vista);
        vista.setVisible(true);
    }

    private void salir() {
        System.exit(0);
    }
}
