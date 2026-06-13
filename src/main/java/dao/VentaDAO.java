
package dao;

import com.ues.group.arbolb.ArbolBusqueda;
import conexion.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import modelo.DetalleVenta;
import interfazDao.IVentaDAO;
import modelo.Venta;

/**
 *
 * @author natha
 */
public class VentaDAO implements IVentaDAO {

    private static final String INSERT_VENTA = "INSERT INTO public.ventas (fecha, hora, id_cliente, subtotal, total) " + "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_VENTAS = "SELECT v.id_ventas, v.fecha, v.hora, v.id_cliente, " + "v.subtotal, v.total, "
            + "c.nombre AS nombre_cliente, c.apellido AS apellido_cliente " + "FROM public.ventas v " + "JOIN public.clientes c ON v.id_cliente = c.id_clientes "
            + "ORDER BY v.id_ventas";

    private static final String SELECT_VENTA_ID = "SELECT v.id_ventas, v.fecha, v.hora, v.id_cliente, " + "v.subtotal, v.total " + "FROM public.ventas v "
            + "WHERE v.id_ventas = ?";

    private static final String DELETE_VENTA = "DELETE FROM public.ventas WHERE id_ventas = ?";

    private static final String INSERT_DETALLE = "INSERT INTO public.detalle_ventas (id_venta, id_producto, cantidad, precio, subtotal) " + "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_DETALLES_POR_VENTA = "SELECT dv.id_detalleventas, dv.id_venta, dv.id_producto, " + "dv.cantidad, dv.precio, dv.subtotal, "
            + "p.descripcion AS descripcion_producto " + "FROM public.detalle_ventas dv " + "JOIN public.productos p ON dv.id_producto = p.id_productos "
            + "WHERE dv.id_venta = ?";

    private static final String DELETE_DETALLES_POR_VENTA = "DELETE FROM public.detalle_ventas WHERE id_venta = ?";

    private static final String RESTAR_STOCK = "UPDATE public.productos SET stock = stock - ? WHERE id_productos = ?";

    private static final String DEVOLVER_STOCK = "UPDATE public.productos SET stock = stock + ? WHERE id_productos = ?";

    @Override
    public void registrarVenta(Venta venta) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);

            // 1. Insertar encabezado de venta y recuperar el ID generado
            PreparedStatement psVenta = conn.prepareStatement(INSERT_VENTA, Statement.RETURN_GENERATED_KEYS);
            psVenta.setDate(1, Date.valueOf(venta.getFecha()));
            psVenta.setTime(2, Time.valueOf(venta.getHora()));
            psVenta.setInt(3, venta.getIdCliente());
            psVenta.setBigDecimal(4, venta.getSubtotal());
            psVenta.setBigDecimal(5, venta.getTotal());
            psVenta.executeUpdate();

            ResultSet keys = psVenta.getGeneratedKeys();
            int idVentaGenerado = 0;
            if (keys.next()) {
                idVentaGenerado = keys.getInt(1);
            }

            // Insertar detalle y descontar stock
            PreparedStatement psDetalle = conn.prepareStatement(INSERT_DETALLE);
            PreparedStatement psStock = conn.prepareStatement(RESTAR_STOCK);

            for (DetalleVenta d : venta.getDetalles()) {
                psDetalle.setInt(1, idVentaGenerado);
                psDetalle.setInt(2, d.getIdProducto());
                psDetalle.setInt(3, d.getCantidad());
                psDetalle.setBigDecimal(4, d.getPrecioUnitario());
                psDetalle.setBigDecimal(5, d.getSubtotal());
                psDetalle.addBatch();

                psStock.setInt(1, d.getCantidad());
                psStock.setInt(2, d.getIdProducto());
                psStock.addBatch();
            }

            psDetalle.executeBatch();
            psStock.executeBatch();

            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
    }

    @Override
    public void anularVenta(int idVenta) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);

            //Recuperar detalles para devolver stock antes de eliminar
            List<DetalleVenta> detalles = listarDetalles(idVenta);

            PreparedStatement psStock = conn.prepareStatement(DEVOLVER_STOCK);
            for (DetalleVenta d : detalles) {
                psStock.setInt(1, d.getCantidad());
                psStock.setInt(2, d.getIdProducto());
                psStock.addBatch();
            }
            psStock.executeBatch();

            //Eliminar detalles (también los borra el ON DELETE CASCADE
            PreparedStatement psDetalles = conn.prepareStatement(DELETE_DETALLES_POR_VENTA);
            psDetalles.setInt(1, idVenta);
            psDetalles.executeUpdate();

            //Eliminar encabezado
            PreparedStatement psVenta = conn.prepareStatement(DELETE_VENTA);
            psVenta.setInt(1, idVenta);
            psVenta.executeUpdate();

            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
    }

    @Override
    public ArbolBusqueda<Venta> listar() throws Exception {
        ArbolBusqueda<Venta> arbol = new ArbolBusqueda<>();
        Connection conn = Conexion.getConexion();
        PreparedStatement ps = conn.prepareStatement(SELECT_ALL_VENTAS);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Venta v = mapearVenta(rs);
            arbol.insertar(v);   // ordenado por fecha+id (compareTo de Venta)
        }
        conn.close();
        return arbol;
    }

    //Busca venta ID
    @Override
    public Venta buscar(int idVenta) throws Exception {
        Connection conn = Conexion.getConexion();
        PreparedStatement ps = conn.prepareStatement(SELECT_VENTA_ID);
        ps.setInt(1, idVenta);
        ResultSet rs = ps.executeQuery();
        Venta v = null;
        if (rs.next()) {
            v = mapearVenta(rs);
        }
        conn.close();
        return v;
    }

    // Retorna los detalles de una venta específica.
    @Override
    public List<DetalleVenta> listarDetalles(int idVenta) throws Exception {
        List<DetalleVenta> lista = new ArrayList<>();
        Connection conn = Conexion.getConexion();
        PreparedStatement ps = conn.prepareStatement(SELECT_DETALLES_POR_VENTA);
        ps.setInt(1, idVenta);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            DetalleVenta d = new DetalleVenta();
            d.setIdDetalle(rs.getInt("id_detalleventas"));
            d.setIdVenta(rs.getInt("id_venta"));
            d.setIdProducto(rs.getInt("id_producto"));
            d.setCantidad(rs.getInt("cantidad"));
            d.setPrecioUnitario(rs.getBigDecimal("precio"));
            lista.add(d);
        }
        conn.close();
        return lista;
    }

    private Venta mapearVenta(ResultSet rs) throws Exception {
        Venta v = new Venta();
        v.setIdVenta(rs.getInt("id_ventas"));
        v.setFecha(rs.getDate("fecha").toLocalDate());
        v.setHora(rs.getTime("hora").toLocalTime());
        v.setIdCliente(rs.getInt("id_cliente"));
        v.setSubtotal(rs.getBigDecimal("subtotal"));
        v.setTotal(rs.getBigDecimal("total"));
        return v;
    }
}
