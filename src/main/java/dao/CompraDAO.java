/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.CompraProveedor;
import modelo.DetalleCompra;
import modelo.Producto;
import modelo.Proveedor;

/**
 *
 * @author alexi
 */
public class CompraDAO {
    // private static final String INSERT_DETALLE = "INSERT INTO detalle_compras (cantidad, precio, id_compra, id_producto) VALUES (?, ?, ?, ?)";
    private static final String INSERT_COMPRA = "INSERT INTO compras (fecha, total_compra) VALUES (?, ?)";
    private static final String INSERT_DETALLE = "INSERT INTO detalle_compras (cantidad, precio, id_compra, id_producto, categoria, id_proveedor) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_PROVEEDORES = "SELECT id_proveedor, nombre FROM proveedores ORDER BY nombre";
    private static final String SELECT_HISTORIAL = "SELECT id_compra, fecha, total_compra FROM compras ORDER BY id_compra DESC";

    public int registrar(CompraProveedor compra) throws Exception {
        int idGenerado = -1;
        Connection conn = Conexion.getConexion();
        
        try {
            if (conn == null) {
                System.out.println("adadadsda");
            }
            conn.setAutoCommit(false);
            // conn.setAutoCommit(true);
            PreparedStatement psCompra = conn.prepareStatement(INSERT_COMPRA, Statement.RETURN_GENERATED_KEYS);
            psCompra.setDate(1, Date.valueOf(compra.getFecha()));
            psCompra.setDouble(2, compra.getTotalCompra());
            psCompra.executeUpdate();

            ResultSet keys = psCompra.getGeneratedKeys();
            if (keys.next()) {
                idGenerado = keys.getInt(1);
                compra.setIdCompra(idGenerado);
            }

            PreparedStatement psDetalle = conn.prepareStatement(INSERT_DETALLE);
            for (DetalleCompra d : compra.getDetalles()) {
                psDetalle.setInt(1, d.getCantidad());
                psDetalle.setDouble(2, d.getPrecioUnitario());
                psDetalle.setInt(3, idGenerado);
                psDetalle.setInt(4, d.getIdProducto());
                psDetalle.setString(5, compra.getCategoria());
                psDetalle.setInt(6, d.getIdProveedor());
                psDetalle.executeUpdate();
            }

            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
        
        return idGenerado;
    }

    public List<DetalleCompra> buscarDetalles(int idCompra) throws Exception {
    List<DetalleCompra> datos = new ArrayList<>();
    String consulta = """
            SELECT dc.id_detallecompra, dc.cantidad, dc.precio, dc.id_compra, 
                   dc.id_producto, p.descripcion, prov.nombre AS nombre_proveedor, dc.id_proveedor 
            FROM detalle_compras dc 
            JOIN productos p ON dc.id_producto = p.id_productos 
            JOIN proveedores prov ON dc.id_proveedor = prov.id_proveedor 
            WHERE dc.id_compra = ? ORDER BY dc.id_detallecompra""";
    try (Connection conexion = Conexion.getConexion();
         PreparedStatement ps = conexion.prepareStatement(consulta)) {
        ps.setInt(1, idCompra);
        
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DetalleCompra d = new DetalleCompra();
                Producto p = new Producto();
                Proveedor prov = new Proveedor();

                d.setIdDetalle(rs.getInt("id_detallecompra"));
                d.setCantidad(rs.getInt("cantidad"));
                d.setPrecioUnitario(rs.getDouble("precio"));
                d.setIdCompra(rs.getInt("id_compra"));
                d.setIdProducto(rs.getInt("id_producto"));

                p.setIdProducto(rs.getInt("id_producto"));
                p.setDescripcion(rs.getString("descripcion"));
                d.setProducto(p);

                prov.setIdProveedor(rs.getInt("id_proveedor"));
                prov.setNombre(rs.getString("nombre_proveedor"));
                d.setProveedor(prov);

                datos.add(d);
            }
        }
    } catch (Exception e) {
        System.out.println(e.getMessage());
        throw e;
    }
    
    return datos;
}

    public List<Proveedor> listarProveedores() throws Exception {
        List<Proveedor> lista = new ArrayList<>();
        Connection conn = Conexion.getConexion();
        PreparedStatement ps = conn.prepareStatement(SELECT_PROVEEDORES);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            Proveedor pv = new Proveedor();
            pv.setIdProveedor(rs.getInt("id_proveedor"));
            pv.setNombre(rs.getString("nombre"));
            lista.add(pv);
        }
        
        conn.close();
        return lista;
    }

    public List<CompraProveedor> listarHistorialCompras() throws Exception {
        List<CompraProveedor> lista = new ArrayList<>();
        Connection conn = Conexion.getConexion(); 
        PreparedStatement ps = conn.prepareStatement(SELECT_HISTORIAL);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            CompraProveedor c = new CompraProveedor();
            c.setIdCompra(rs.getInt("id_compra"));
            c.setTotalCompra(rs.getDouble("total_compra"));
            
            if (rs.getDate("fecha") != null) {
                c.setFecha(rs.getDate("fecha").toLocalDate());
            }
            
            lista.add(c);
        }
        
        conn.close();
        return lista;
    }
}