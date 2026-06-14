package dao;

import Arboles.ArbolBusqueda;
import Arboles.Nodo;
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

    private static final String INSERT = "INSERT INTO inventario (nombre_producto, precio_unitario, descripcion, stock_disponible, id_producto) "
            + "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_ALL = "SELECT * FROM inventario ORDER BY id_inventario";

    private static final String UPDATE = "UPDATE inventario SET nombre_producto = ?, precio_unitario = ?, descripcion = ?, "
            + "stock_disponible = ?, id_producto = ? WHERE id_inventario = ?";

    private static final String DELETE = "DELETE FROM inventario WHERE id_inventario = ?";

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

    private ArbolBusqueda<Inventario> construirArbol(String criterio) throws Exception {
        String crit = (criterio == null || criterio.isEmpty()) ? "ID" : criterio;
        ArbolBusqueda<Inventario> arbol = new ArbolBusqueda<>();
        Connection conn = Conexion.getConexion();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Inventario inv = mapear(rs);
                inv.setCriterioOrden(crit);
                arbol.insertar(inv);
            }
        } finally {
            conn.close();
        }
        return arbol;
    }

    @Override
    public ArbolBusqueda<Inventario> listarOrdenado(String criterio) throws Exception {
        return construirArbol(criterio);
    }

    @Override
    public Inventario buscar(int idInventario) throws Exception {
        ArbolBusqueda<Inventario> arbol = construirArbol("ID");

        Inventario clave = new Inventario();
        clave.setIdInventario(idInventario);
        clave.setCriterioOrden("ID");

        Nodo encontrado = arbol.buscar(clave);
        return encontrado != null ? (Inventario) encontrado.getDato() : null;
    }

    @Override
    public ArbolBusqueda<Inventario> buscarPorNombre(String texto) throws Exception {
        ArbolBusqueda<Inventario> base = construirArbol("Nombre A-Z");
        ArbolBusqueda<Inventario> resultado = new ArbolBusqueda<>();

        Inventario clave = new Inventario();
        clave.setNombreProducto(texto);
        clave.setCriterioOrden("Nombre A-Z");

        Nodo encontrado = base.buscar(clave);
        if (encontrado != null) {
            resultado.insertar((Inventario) encontrado.getDato());
        } else {
            filtrarPorNombre(base.getRaiz(), texto.toLowerCase(), resultado);
        }
        return resultado;
    }

    private void filtrarPorNombre(Nodo r, String textoBusq, ArbolBusqueda<Inventario> resultado) {
        if (r != null) {
            filtrarPorNombre(r.getRamaIzq(), textoBusq, resultado);
            Inventario inv = (Inventario) r.getDato();
            String nombre = inv.getNombreProducto();
            if (nombre != null && nombre.toLowerCase().contains(textoBusq)) {
                resultado.insertar(inv);
            }
            filtrarPorNombre(r.getRamaDrch(), textoBusq, resultado);
        }
    }

    @Override
    public List<Inventario> listar() throws Exception {
        ArbolBusqueda<Inventario> arbol = construirArbol("ID");
        List<Inventario> resultado = new ArrayList<>();
        aplanarInOrden(arbol.getRaiz(), resultado);
        return resultado;
    }

    private void aplanarInOrden(Nodo r, List<Inventario> destino) {
        if (r != null) {
            aplanarInOrden(r.getRamaIzq(), destino);
            destino.add((Inventario) r.getDato());
            aplanarInOrden(r.getRamaDrch(), destino);
        }
    }

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
