
package dao;

import Arboles.ArbolBusqueda;
import conexion.Conexion;
import interfaz.IVentaDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import modelo.DetalleVenta;
import modelo.Cliente;
import modelo.Producto;
import modelo.Venta;

public class VentaDAO implements IVentaDAO {

    private static final String INSERT_VENTA = "INSERT INTO public.ventas (fecha, hora, id_cliente, subtotal, total) "
            + "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_VENTAS = "SELECT v.id_ventas, v.fecha, v.hora, v.id_cliente, v.subtotal, v.total, "
            + "c.nombre AS nombre_cliente, c.apellido AS apellido_cliente "
            + "FROM public.ventas v "
            + "JOIN public.cliente c ON v.id_cliente = c.id_cliente "
            + "ORDER BY v.fecha, v.id_ventas";

    private static final String SELECT_VENTA_ID = "SELECT v.id_ventas, v.fecha, v.hora, v.id_cliente, v.subtotal, v.total "
            + "FROM public.ventas v "
            + "WHERE v.id_ventas = ?";

    private static final String DELETE_VENTA = "DELETE FROM public.ventas WHERE id_ventas = ?";

    private static final String INSERT_DETALLE = "INSERT INTO public.detalle_venta (id_venta, id_producto, cantidad, precio, subtotal) "
            + "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_DETALLES_POR_VENTA = "SELECT dv.id_detalleventas, dv.id_venta, dv.id_producto, "
            + "dv.cantidad, dv.precio, dv.subtotal, "
            + "p.descripcion AS descripcion_producto "
            + "FROM public.detalle_venta dv "
            + "JOIN public.productos p ON dv.id_producto = p.id_productos "
            + "WHERE dv.id_venta = ?";

    private static final String DELETE_DETALLES_POR_VENTA = "DELETE FROM public.detalle_venta WHERE id_venta = ?";

    private static final String RESTAR_STOCK = "UPDATE public.productos SET stock = stock - ? WHERE id_productos = ?";

    private static final String DEVOLVER_STOCK = "UPDATE public.productos SET stock = stock + ? WHERE id_productos = ?";

    private static final String RESTAR_STOCK_INVENTARIO = "UPDATE public.inventario SET stock_disponible = stock_disponible - ? WHERE id_producto = ?";

    private static final String DEVOLVER_STOCK_INVENTARIO = "UPDATE public.inventario SET stock_disponible = stock_disponible + ? WHERE id_producto = ?";

    @Override
    public void registrarVenta(Venta venta) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);

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

            PreparedStatement psDetalle = conn.prepareStatement(INSERT_DETALLE);
            PreparedStatement psStock = conn.prepareStatement(RESTAR_STOCK);
            PreparedStatement psStockInv = conn.prepareStatement(RESTAR_STOCK_INVENTARIO);

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

                psStockInv.setInt(1, d.getCantidad());
                psStockInv.setInt(2, d.getIdProducto());
                psStockInv.addBatch();
            }

            psDetalle.executeBatch();
            psStock.executeBatch();
            psStockInv.executeBatch();

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

            List<DetalleVenta> detalles = listarDetalles(idVenta);

            PreparedStatement psStock = conn.prepareStatement(DEVOLVER_STOCK);
            PreparedStatement psStockInv = conn.prepareStatement(DEVOLVER_STOCK_INVENTARIO);

            for (DetalleVenta d : detalles) {
                psStock.setInt(1, d.getCantidad());
                psStock.setInt(2, d.getIdProducto());
                psStock.addBatch();

                psStockInv.setInt(1, d.getCantidad());
                psStockInv.setInt(2, d.getIdProducto());
                psStockInv.addBatch();
            }

            psStock.executeBatch();
            psStockInv.executeBatch();

            PreparedStatement psDetalles = conn.prepareStatement(DELETE_DETALLES_POR_VENTA);
            psDetalles.setInt(1, idVenta);
            psDetalles.executeUpdate();

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
            arbol.insertar(mapearVenta(rs));
        }
        conn.close();
        return arbol;
    }

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

            Producto p = new Producto();
            p.setIdProducto(rs.getInt("id_producto"));
            p.setDescripcion(rs.getString("descripcion_producto"));
            d.setProducto(p);

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

        Cliente c = new Cliente();
        c.setNombre(rs.getString("nombre_cliente"));
        c.setApellido(rs.getString("apellido_cliente"));
        v.setCliente(c);

        return v;
    }
}
