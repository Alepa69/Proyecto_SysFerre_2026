package dao;

import Arboles.ArbolAVL;
import conexion.Conexion;
import interfaz.IProductoDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import Arboles.ArbolBusqueda;
import modelo.Producto;

public class ProductoDAO implements IProductoDAO {

    private static final String INSERT = "INSERT INTO productos (descripcion, tipo, precio, stock) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL = "SELECT * FROM productos ORDER BY id_productos";
    private static final String SELECT_ID = "SELECT * FROM productos WHERE id_productos = ?";
    private static final String UPDATE = "UPDATE productos SET descripcion = ?, tipo = ?, precio = ?, stock = ? WHERE id_productos = ?";
    private static final String DELETE = "DELETE FROM productos WHERE id_productos = ?";

    public void insertar(Producto p) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(INSERT);
            ps.setString(1, p.getDescripcion());
            ps.setString(2, p.getTipo());
            ps.setBigDecimal(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.executeUpdate();
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
    }

    public void actualizar(Producto p) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(UPDATE);
            ps.setString(1, p.getDescripcion());
            ps.setString(2, p.getTipo());
            ps.setBigDecimal(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getIdProducto());
            ps.executeUpdate();
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
    }

    public void eliminar(int idProducto) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(DELETE);
            ps.setInt(1, idProducto);
            ps.executeUpdate();
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
    }

    public ArbolAVL<Producto> listar() throws Exception {
        ArbolAVL<Producto> arbol = new ArbolAVL<>();
        Connection conn = Conexion.getConexion();
        PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Producto p = new Producto();
            p.setIdProducto(rs.getInt("id_productos"));
            p.setDescripcion(rs.getString("descripcion"));
            p.setTipo(rs.getString("tipo"));
            p.setPrecio(rs.getBigDecimal("precio"));
            p.setStock(rs.getInt("stock"));
            arbol.insertar(p);
        }
        conn.close();
        return arbol;
    }

    public Producto buscar(int idProducto) throws Exception {
        Connection conn = Conexion.getConexion();
        PreparedStatement ps = conn.prepareStatement(SELECT_ID);
        ps.setInt(1, idProducto);
        ResultSet rs = ps.executeQuery();
        Producto p = null;
        if (rs.next()) {
            p = new Producto();
            p.setIdProducto(rs.getInt("id_productos"));
            p.setDescripcion(rs.getString("descripcion"));
            p.setTipo(rs.getString("tipo"));
            p.setPrecio(rs.getBigDecimal("precio"));
            p.setStock(rs.getInt("stock"));
        }
        conn.close();
        return p;
    }
}