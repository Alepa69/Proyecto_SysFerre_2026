/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import conexion.Conexion;
import interfaz.IInventarioDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import modelo.Inventario;

/**
 *
 * @author mendo
 */
public class InventarioDAO implements IInventarioDAO {

    private static final String INSERT
            = "INSERT INTO inventario (nombre_producto, precio_unitario, descripcion, stock_disponible, id_producto) "
            + "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_ALL
            = "SELECT * FROM inventario ORDER BY id_inventario";

    private static final String SELECT_ID
            = "SELECT * FROM inventario WHERE id_inventario = ?";

    private static final String UPDATE
            = "UPDATE inventario SET nombre_producto = ?, precio_unitario = ?, descripcion = ?, "
            + "stock_disponible = ?, id_producto = ? WHERE id_inventario = ?";

    private static final String DELETE
            = "DELETE FROM inventario WHERE id_inventario = ?";

    private static final String SELECT_NOMBRE
            = "SELECT * FROM inventario WHERE LOWER(nombre_producto) LIKE LOWER(?) "
            + "OR LOWER(descripcion) LIKE LOWER(?) ORDER BY nombre_producto";

    @Override
    public void insertar(Inventario i) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(INSERT);
            ps.setString(1, i.getNombreProducto());
            ps.setBigDecimal(2, i.getPrecioUnitario());
            ps.setString(3, i.getDescripcion());
            ps.setInt(4, i.getStockDisponible());
            ps.setInt(5, i.getIdProducto());
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
    public void actualizar(Inventario i) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(UPDATE);
            ps.setString(1, i.getNombreProducto());
            ps.setBigDecimal(2, i.getPrecioUnitario());
            ps.setString(3, i.getDescripcion());
            ps.setInt(4, i.getStockDisponible());
            ps.setInt(5, i.getIdProducto());
            ps.setInt(6, i.getIdInventario());
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
    public void eliminar(int idInventario) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(DELETE);
            ps.setInt(1, idInventario);
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
    public List<Inventario> listar() throws Exception {
        List<Inventario> lista = new ArrayList<>();
        Connection conn = Conexion.getConexion();
        PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            lista.add(mapear(rs));
        }
        conn.close();
        return lista;
    }

    @Override
    public Inventario buscar(int idInventario) throws Exception {
        Connection conn = Conexion.getConexion();
        PreparedStatement ps = conn.prepareStatement(SELECT_ID);
        ps.setInt(1, idInventario);
        ResultSet rs = ps.executeQuery();
        Inventario i = null;
        if (rs.next()) {
            i = mapear(rs);
        }
        conn.close();
        return i;
    }

    @Override
    public List<Inventario> buscarPorNombre(String texto) throws Exception {
        List<Inventario> lista = new ArrayList<>();
        Connection conn = Conexion.getConexion();
        PreparedStatement ps = conn.prepareStatement(SELECT_NOMBRE);
        String filtro = "%" + texto + "%";
        ps.setString(1, filtro);
        ps.setString(2, filtro);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            lista.add(mapear(rs));
        }
        conn.close();
        return lista;
    }

    @Override
    public List<Inventario> listarOrdenado(String criterio) throws Exception {
        String orden;
        switch (criterio) {
            case "Stock Ascendente":
                orden = "stock_disponible ASC";
                break;
            case "Stock Descendente":
                orden = "stock_disponible DESC";
                break;
            case "Nombre A-Z":
                orden = "nombre_producto ASC";
                break;
            case "Precio":
                orden = "precio_unitario ASC";
                break;
            default:
                orden = "id_inventario ASC";
        }

        List<Inventario> lista = new ArrayList<>();
        Connection conn = Conexion.getConexion();
       
        String sql = "SELECT * FROM inventario ORDER BY " + orden;
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            lista.add(mapear(rs));
        }
        conn.close();
        return lista;
    }

    // Método auxiliar para no repetir el mapeo en cada consulta
    private Inventario mapear(ResultSet rs) throws Exception {
        Inventario i = new Inventario();
        i.setIdInventario(rs.getInt("id_inventario"));
        i.setNombreProducto(rs.getString("nombre_producto"));
        i.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        i.setDescripcion(rs.getString("descripcion"));
        i.setStockDisponible(rs.getInt("stock_disponible"));
        i.setIdProducto(rs.getInt("id_producto"));
        return i;
    }
}
