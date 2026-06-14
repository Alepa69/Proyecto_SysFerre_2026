
package dao;

import conexion.Conexion;
import interfaz.IClienteDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Arboles.ArbolBusqueda;
import modelo.Cliente;

/**
 *
 * @author natha
 */
public class ClienteDAO implements IClienteDAO {

    private static final String INSERT = "INSERT INTO cliente (nombre, apellido) VALUES (?, ?)";
    private static final String SELECT_ALL = "SELECT * FROM cliente ORDER BY id_cliente";
    private static final String SELECT_ID = "SELECT * FROM cliente WHERE id_cliente = ?";
    private static final String UPDATE = "UPDATE cliente SET nombre = ?, apellido = ? WHERE id_cliente = ?";
    private static final String DELETE = "DELETE FROM cliente WHERE id_cliente = ?";

    @Override
    public void insertar(Cliente c) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(INSERT);
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.executeUpdate();
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
    }

    @Override
    public void actualizar(Cliente c) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(UPDATE);
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setInt(3, c.getIdCliente());
            ps.executeUpdate();
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
    }

    @Override
    public void eliminar(int idCliente) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(DELETE);
            ps.setInt(1, idCliente);
            ps.executeUpdate();
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
    }

    @Override
    public ArbolBusqueda<Cliente> listar() throws Exception {
        ArbolBusqueda<Cliente> arbol = new ArbolBusqueda<>();
        Connection conn = Conexion.getConexion();
        PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Cliente c = new Cliente();
            c.setIdCliente(rs.getInt("id_cliente"));
            c.setNombre(rs.getString("nombre"));
            c.setApellido(rs.getString("apellido"));
            arbol.insertar(c); // se inserta ordenado por nombre (compareTo)
        }
        conn.close();
        return arbol;
    }

    @Override
    public Cliente buscar(int idCliente) throws Exception {
        Connection conn = Conexion.getConexion();
        PreparedStatement ps = conn.prepareStatement(SELECT_ID);
        ps.setInt(1, idCliente);
        ResultSet rs = ps.executeQuery();
        Cliente c = null;
        if (rs.next()) {
            c = new Cliente();
            c.setIdCliente(rs.getInt("id_cliente"));
            c.setNombre(rs.getString("nombre"));
            c.setApellido(rs.getString("apellido"));
        }
        conn.close();
        return c;
    }
}
