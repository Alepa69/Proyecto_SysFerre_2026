/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.ues.group.vista.VistaClientes;
import com.ues.group.vista.VistaCompras;
import com.ues.group.vista.VistaEmpleados;
import com.ues.group.vista.VistaInventario;
import com.ues.group.vista.VistaMenuPrincipal;
import com.ues.group.vista.VistaProductos;
import com.ues.group.vista.VistaProveedores;
import com.ues.group.vista.VistaReporte;
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
        this.vista.getBtnReportes().addActionListener(e -> llamarReportes());
        this.vista.getBtnVentas().addActionListener(e -> llamarVentas());
        this.vista.getBtnSalir().addActionListener(e -> salir());
    }

    // Métodos para llamar a cada submenu
    private void llamarClientes() {
        VistaClientes vc = new VistaClientes();
        new ControladorClientes(vc);
        vc.setVisible(true);
        // vista.dispose();
    }

    private void llamarCompras() {
        VistaCompras vc = new VistaCompras();
        // new ControladorCompras(vc);
        vc.setVisible(true);
        // vista.dispose();
    }

    private void llamarEmpleados() {
        VistaEmpleados ve = new VistaEmpleados();
        // new ControladorEmpleados(ve);
        ve.setVisible(true);
        // vista.dispose();
    }

    private void llamarInventario() {
        VistaInventario vi = new VistaInventario();
        // new ControladorInventario(vi);
        vi.setVisible(true);
        // vista.dispose();
    }

    private void llamarProductos() {
        VistaProductos vp = new VistaProductos();
        new ControladorProducto(vp);
        vp.setVisible(true);
        // vista.dispose();
    }

    private void llamarProveedores() {
        VistaProveedores vp = new VistaProveedores();
        new ControladorProveedores(vp);
        vp.setVisible(true);
        vista.dispose();
    }

    private void llamarReportes() {
        VistaReporte vr = new VistaReporte();
        // new ControladorReporte(vr);
        vr.setVisible(true);
        // vista.dispose();
    }

    private void llamarVentas() {
        VistaVentas vv = new VistaVentas();
        new ControladorVentas(vv);
        vv.setVisible(true);
        // vista.dispose();
    }

    private void salir() {
        System.exit(0);
    }
}