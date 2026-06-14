package dao;

import conexion.Conexion;
import interfaz.IProveedoresDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Arboles.ArbolBusqueda;
import modelo.Proveedor;

public class ProveedoresDAO implements IProveedoresDAO {

    private static final String INSERT = "INSERT INTO proveedores (nombre, nit, telefono, correo, direccion) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ALL = "SELECT * FROM proveedores ORDER BY id_proveedores";
    private static final String SELECT_ID = "SELECT * FROM proveedores WHERE id_proveedores = ?";
    private static final String UPDATE = "UPDATE proveedores SET nombre = ?, nit = ?, telefono = ?, correo = ?, direccion = ? WHERE id_proveedores = ?";
    private static final String DELETE = "DELETE FROM proveedores WHERE id_proveedores = ?";

    public void insertar(Proveedor p) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(INSERT);
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getNit());
            ps.setString(3, p.getTelefonos());
            ps.setString(4, p.getCorreo());
            ps.setString(5, p.getDireccion());
            ps.executeUpdate();
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
    }

    public void actualizar(Proveedor p) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(UPDATE);
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getNit());
            ps.setString(3, p.getTelefonos());
            ps.setString(4, p.getCorreo());
            ps.setString(5, p.getDireccion());
            ps.setInt(6, p.getIdProveedor());
            ps.executeUpdate();
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
    }

    public void eliminar(int idProveedor) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(DELETE);
            ps.setInt(1, idProveedor);
            ps.executeUpdate();
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
    }

    // MODIFICACIÓN: Retornamos ArbolBusqueda e insertamos los nodos
    public ArbolBusqueda<Proveedor> listar() throws Exception {
        ArbolBusqueda<Proveedor> lista = new ArbolBusqueda<>();
        Connection conn = Conexion.getConexion();
        PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Proveedor c = new Proveedor();
            c.setIdProveedor(rs.getInt("id_proveedores"));
            c.setNombre(rs.getString("nombre"));
            c.setNit(rs.getString("nit"));
            c.setTelefonos(rs.getString("telefono"));
            c.setCorreo(rs.getString("correo"));
            c.setDireccion(rs.getString("direccion"));
            lista.insertar(c); // Se inserta ordenándose por nombre (según tu compareTo)

        }
        conn.close();
        return lista;
    }

    public Proveedor buscar(int idProveedor) throws Exception {
        Connection conn = Conexion.getConexion();
        PreparedStatement ps = conn.prepareStatement(SELECT_ID);
        ps.setInt(1, idProveedor);
        ResultSet rs = ps.executeQuery();
        Proveedor c = null;
        if (rs.next()) {
            c = new Proveedor();
            c.setIdProveedor(rs.getInt("id_proveedores"));
            c.setNombre(rs.getString("nombre"));
            c.setNit(rs.getString("nit"));
            c.setTelefonos(rs.getString("telefono"));
            c.setCorreo(rs.getString("correo"));
            c.setDireccion(rs.getString("direccion"));
        }
        conn.close();
        return c;
    }

    // // Mapea un ResultSet a un objeto Proveedor
    // private Proveedor mapear(ResultSet rs) throws Exception {
    // Proveedor p = new Proveedor();
    // p.setIdProveedor(rs.getInt("id_proveedores"));
    // p.setNombre(rs.getString("nombre"));
    // p.setNit(rs.getString("nit"));
    // p.setTelefonos(rs.getString("telefono"));
    // p.setCorreo(rs.getString("correo"));
    // p.setDireccion(rs.getString("direccion"));
    // return p;
    // }
}