package dao;

import Arboles.ArbolBusqueda; // Nueva importación
import conexion.Conexion;
import interfaz.IEmpleadoDAO; // O interfazDao.IEmpleadoDAO según la estructura exacta de tu proyecto
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import modelo.Empleado;
import utileria.Encriptar;

public class EmpleadoDAO implements IEmpleadoDAO {

    private static final String INSERT_EMPLEADO = "INSERT INTO empleado (nombres, apellidos, telefono, correo, direccion) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_USUARIO = "INSERT INTO usuarios (usuario, contrasena, id_empleado) VALUES (?, ?, ?)";
    private static final String SELECT_ALL = "SELECT e.id_empleado, e.nombres, e.apellidos, e.telefono, e.correo, e.direccion, "
            + "u.usuario, u.contrasena "
            + "FROM empleado e "
            + "LEFT JOIN usuarios u ON u.id_empleado = e.id_empleado "
            + "ORDER BY e.id_empleado";
    private static final String SELECT_ID = SELECT_ALL.replace("ORDER BY e.id_empleado", "WHERE e.id_empleado = ?");
    private static final String SELECT_USUARIO_ID = "SELECT id_usuario FROM usuarios WHERE id_empleado = ?";
    private static final String UPDATE_EMPLEADO = "UPDATE empleado SET nombres = ?, apellidos = ?, telefono = ?, correo = ?, direccion = ? WHERE id_empleado = ?";
    private static final String UPDATE_USUARIO = "UPDATE usuarios SET usuario = ?, contrasena = ? WHERE id_empleado = ?";
    private static final String DELETE_USUARIO = "DELETE FROM usuarios WHERE id_empleado = ?";
    private static final String DELETE_EMPLEADO = "DELETE FROM empleado WHERE id_empleado = ?";

    @Override
    public void insertar(Empleado empleado) throws Exception {
        Connection conn = Conexion.getConexion();
        validarConexion(conn);
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(INSERT_EMPLEADO, Statement.RETURN_GENERATED_KEYS)) {
                cargarEmpleado(ps, empleado);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        empleado.setIdEmpleado(rs.getInt(1));
                    }
                }
            }

            try (PreparedStatement psUsuario = conn.prepareStatement(INSERT_USUARIO)) {
                psUsuario.setString(1, empleado.getNombreUsuario());
                /* Agregando el hash de la contraseña */
                psUsuario.setString(2, utileria.Encriptar.getStringMessageDialog(
                        empleado.getContrasena(), utileria.Encriptar.SHA256));
                psUsuario.setInt(3, empleado.getIdEmpleado());
                psUsuario.executeUpdate();
            }
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
    }

    @Override
    public void actualizar(Empleado empleado) throws Exception {
        Connection conn = Conexion.getConexion();
        validarConexion(conn);
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(UPDATE_EMPLEADO)) {
                cargarEmpleado(ps, empleado);
                ps.setInt(6, empleado.getIdEmpleado());
                ps.executeUpdate();
            }

            if (existeUsuario(conn, empleado.getIdEmpleado())) {
                // Si la contra va vacia no se actualiza
                boolean cambiarContrasena = empleado.getContrasena() != null
                        && !empleado.getContrasena().trim().isEmpty();

                String sql = cambiarContrasena
                        ? "UPDATE usuarios SET usuario = ?, contrasena = ? WHERE id_empleado = ?"
                        : "UPDATE usuarios SET usuario = ? WHERE id_empleado = ?";

                try (PreparedStatement psUsuario = conn.prepareStatement(sql)) {
                    psUsuario.setString(1, empleado.getNombreUsuario());
                    if (cambiarContrasena) {
                        psUsuario.setString(2, utileria.Encriptar.getStringMessageDialog(
                                empleado.getContrasena().trim(), utileria.Encriptar.SHA256));
                        psUsuario.setInt(3, empleado.getIdEmpleado());
                    } else {
                        psUsuario.setInt(2, empleado.getIdEmpleado());
                    }
                    psUsuario.executeUpdate();
                }
            } else {
                // inserta normalmente
                try (PreparedStatement psUsuario = conn.prepareStatement(INSERT_USUARIO)) {
                    psUsuario.setString(1, empleado.getNombreUsuario());
                    psUsuario.setString(2, utileria.Encriptar.getStringMessageDialog(
                            empleado.getContrasena(), utileria.Encriptar.SHA256));
                    psUsuario.setInt(3, empleado.getIdEmpleado());
                    psUsuario.executeUpdate();
                }
            }
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
    }

    @Override
    public void eliminar(int idEmpleado) throws Exception {
        Connection conn = Conexion.getConexion();
        validarConexion(conn);
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement psUsuario = conn.prepareStatement(DELETE_USUARIO)) {
                psUsuario.setInt(1, idEmpleado);
                psUsuario.executeUpdate();
            }
            try (PreparedStatement psEmpleado = conn.prepareStatement(DELETE_EMPLEADO)) {
                psEmpleado.setInt(1, idEmpleado);
                psEmpleado.executeUpdate();
            }
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
    }

    @Override
    public ArbolBusqueda<Empleado> listar() throws Exception {
        ArbolBusqueda<Empleado> arbol = new ArbolBusqueda<>();
        Connection conn = Conexion.getConexion();
        validarConexion(conn);
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                arbol.insertar(mapear(rs));
            }
        } finally {
            conn.close();
        }
        return arbol;
    }

    @Override
    public Empleado buscar(int idEmpleado) throws Exception {
        Connection conn = Conexion.getConexion();
        validarConexion(conn);
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ID)) {
            ps.setInt(1, idEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } finally {
            conn.close();
        }
        return null;
    }

    private void cargarEmpleado(PreparedStatement ps, Empleado empleado) throws Exception {
        ps.setString(1, empleado.getNombre());
        ps.setString(2, empleado.getApellido());
        ps.setString(3, empleado.getTelefono());
        ps.setString(4, empleado.getCorreo());
        ps.setString(5, empleado.getDireccion());
    }

    private boolean existeUsuario(Connection conn, int idEmpleado) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_USUARIO_ID)) {
            ps.setInt(1, idEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Empleado mapear(ResultSet rs) throws Exception {
        Empleado empleado = new Empleado();
        empleado.setIdEmpleado(rs.getInt("id_empleado"));
        empleado.setNombre(rs.getString("nombres"));
        empleado.setApellido(rs.getString("apellidos"));
        empleado.setTelefono(rs.getString("telefono"));
        empleado.setCorreo(rs.getString("correo"));
        empleado.setDireccion(rs.getString("direccion"));
        empleado.setNombreUsuario(rs.getString("usuario"));
        empleado.setContrasena(null);
        return empleado;
    }

    private void validarConexion(Connection conn) throws Exception {
        if (conn == null) {
            throw new Exception("No se pudo conectar con la base de datos.");
        }
    }
}