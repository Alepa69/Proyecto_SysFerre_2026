/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import conexion.Conexion;
import interfaz.IInventarioDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Arboles.ArbolBusqueda;
import Arboles.Nodo;
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

    @SuppressWarnings("unchecked")
    @Override
    public List<Inventario> listar() throws Exception {
        List<Inventario> raw = listarDesdeBD();

        ArbolBusqueda<NodoInventario> arbol = new ArbolBusqueda<>();
        for (Inventario inv : raw) {
            arbol.insertar(new NodoInventario(inv, "ID"));
        }

        ArrayList<NodoInventario> inorden = arbol.IND();
        List<Inventario> resultado = new ArrayList<>();
        for (NodoInventario n : inorden) {
            resultado.add(n.inventario);
        }
        return resultado;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Inventario buscar(int idInventario) throws Exception {
        List<Inventario> raw = listarDesdeBD();

        ArbolBusqueda<NodoInventario> arbol = new ArbolBusqueda<>();
        for (Inventario inv : raw) {
            arbol.insertar(new NodoInventario(inv, "ID"));
        }

        Inventario clave = new Inventario();
        clave.setIdInventario(idInventario);
        Nodo encontrado = arbol.buscar(new NodoInventario(clave, "ID"));

        return encontrado != null ? ((NodoInventario) encontrado.getDato()).inventario : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Inventario> buscarPorNombre(String texto) throws Exception {
        List<Inventario> raw = listarDesdeBD();
        List<Inventario> filtrada = new ArrayList<>();

        ArbolBusqueda<NodoInventario> arbol = new ArbolBusqueda<>();
        for (Inventario inv : raw) {
            arbol.insertar(new NodoInventario(inv, "Nombre A-Z"));
        }

        Inventario clave = new Inventario();
        clave.setNombreProducto(texto);
        Nodo encontrado = arbol.buscar(new NodoInventario(clave, "Nombre A-Z"));

        if (encontrado != null) {
            filtrada.add(((NodoInventario) encontrado.getDato()).inventario);
        } else {
            String textoBusq = texto.toLowerCase();
            ArrayList<NodoInventario> inorden = arbol.IND();
            for (NodoInventario n : inorden) {
                if (n.inventario.getNombreProducto() != null
                        && n.inventario.getNombreProducto()
                                .toLowerCase().contains(textoBusq)) {
                    filtrada.add(n.inventario);
                }
            }
        }

        return filtrada;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Inventario> listarOrdenado(String criterio) throws Exception {
        List<Inventario> raw = listarDesdeBD();

        ArbolBusqueda<NodoInventario> arbol = new ArbolBusqueda<>();
        for (Inventario inv : raw) {
            arbol.insertar(new NodoInventario(inv, criterio));
        }

        ArrayList<NodoInventario> inorden = arbol.IND();
        List<Inventario> resultado = new ArrayList<>();
        for (NodoInventario n : inorden) {
            resultado.add(n.inventario);
        }

        if ("Stock Descendente".equals(criterio)) {
            Collections.reverse(resultado);
        }

        return resultado;
    }

    private List<Inventario> listarDesdeBD() throws Exception {
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

    private static class NodoInventario implements Comparable<NodoInventario> {

        final Inventario inventario;
        final String criterio;

        NodoInventario(Inventario inventario, String criterio) {
            this.inventario = inventario;
            this.criterio = criterio;
        }

        @Override
        public int compareTo(NodoInventario otro) {
            int c;
            switch (criterio) {
                case "Nombre A-Z" ->
                    c = compararTexto(inventario.getNombreProducto(),
                            otro.inventario.getNombreProducto());
                case "Precio" ->
                    c = compararPrecio(inventario.getPrecioUnitario(),
                            otro.inventario.getPrecioUnitario());
                case "Stock Ascendente", "Stock Descendente" ->
                    c = Integer.compare(inventario.getStockDisponible(),
                            otro.inventario.getStockDisponible());
                case "ID" ->
                    c = Integer.compare(inventario.getIdInventario(),
                            otro.inventario.getIdInventario());
                default ->
                    c = Integer.compare(inventario.getIdInventario(),
                            otro.inventario.getIdInventario());
            }
            if (c == 0) {
                c = Integer.compare(inventario.getIdInventario(),
                        otro.inventario.getIdInventario());
            }
            return c;
        }

        private int compararTexto(String a, String b) {
            return (a == null ? "" : a).compareToIgnoreCase(b == null ? "" : b);
        }

        private int compararPrecio(BigDecimal a, BigDecimal b) {
            return (a == null ? BigDecimal.ZERO : a)
                    .compareTo(b == null ? BigDecimal.ZERO : b);
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
