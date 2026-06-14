
package dao;



import conexion.Conexion;
import interfaz.IEmpleadoDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import modelo.Empleado;
import modelo.Usuario;

/**
 * DAO para Empleados y Usuarios.
 * Cada empleado puede tener un usuario asociado en la tabla "usuarios".
 */
public class EmpleadoDAO implements IEmpleadoDAO {

    // ── Empleados ────────────────────────────────────────────────────────────
    private static final String INSERT_EMP =
            "INSERT INTO public.empleado (nombres, apellidos, telefono, correo, direccion) "
            + "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_ALL =
            "SELECT e.id_empleado, e.nombres, e.apellidos, e.telefono, e.correo, e.direccion, "
            + "       u.usuario "
            + "FROM public.empleado e "
            + "LEFT JOIN public.usuarios u ON u.id_empleado = e.id_empleado "
            + "ORDER BY e.id_empleado";

    private static final String SELECT_ID =
            "SELECT e.id_empleado, e.nombres, e.apellidos, e.telefono, e.correo, e.direccion "
            + "FROM public.empleado e WHERE e.id_empleado = ?";

    private static final String UPDATE_EMP =
            "UPDATE public.empleado SET nombres = ?, apellidos = ?, telefono = ?, "
            + "correo = ?, direccion = ? WHERE id_empleado = ?";

    private static final String DELETE_EMP =
            "DELETE FROM public.empleado WHERE id_empleado = ?";

    // ── Usuarios ─────────────────────────────────────────────────────────────
    private static final String INSERT_USR =
            "INSERT INTO public.usuarios (usuario, contrasena, id_empleado) VALUES (?, ?, ?)";

    private static final String SELECT_USR_BY_EMP =
            "SELECT id_usuario, usuario, contrasena, id_empleado "
            + "FROM public.usuarios WHERE id_empleado = ?";

    private static final String UPDATE_USR =
            "UPDATE public.usuarios SET usuario = ?, contrasena = ? WHERE id_empleado = ?";

    private static final String DELETE_USR =
            "DELETE FROM public.usuarios WHERE id_empleado = ?";

    private static final String EXISTS_USR =
            "SELECT COUNT(*) FROM public.usuarios WHERE id_empleado = ?";

    // ── Implementación ───────────────────────────────────────────────────────

    /**
     * Inserta un empleado y, si se proporcionan usuario y contraseña, crea su usuario.
     */
    @Override
    public void insertar(Empleado e, String usuario, String contrasena) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);

            // 1. Insertar empleado y obtener id generado
            PreparedStatement psEmp = conn.prepareStatement(INSERT_EMP,
                    java.sql.Statement.RETURN_GENERATED_KEYS);
            psEmp.setString(1, e.getNombre());
            psEmp.setString(2, e.getApellido());
            psEmp.setString(3, e.getTelefono());
            psEmp.setString(4, e.getCorreo());
            psEmp.setString(5, e.getDireccion());
            psEmp.executeUpdate();

            ResultSet rs = psEmp.getGeneratedKeys();
            int idGenerado = 0;
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }

            // 2. Insertar usuario si se proporcionaron credenciales
            if (usuario != null && !usuario.trim().isEmpty()
                    && contrasena != null && !contrasena.trim().isEmpty()) {
                PreparedStatement psUsr = conn.prepareStatement(INSERT_USR);
                psUsr.setString(1, usuario.trim());
                psUsr.setString(2, contrasena.trim());
                psUsr.setInt(3, idGenerado);
                psUsr.executeUpdate();
            }

            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
    }

    /**
     * Actualiza datos del empleado y su usuario (crea el usuario si no existía).
     */
    @Override
    public void actualizar(Empleado e, String usuario, String contrasena) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);

            // 1. Actualizar empleado
            PreparedStatement psEmp = conn.prepareStatement(UPDATE_EMP);
            psEmp.setString(1, e.getNombre());
            psEmp.setString(2, e.getApellido());
            psEmp.setString(3, e.getTelefono());
            psEmp.setString(4, e.getCorreo());
            psEmp.setString(5, e.getDireccion());
            psEmp.setInt(6, e.getIdEmpleado());
            psEmp.executeUpdate();

            // 2. Si hay credenciales, actualizar o crear usuario
            if (usuario != null && !usuario.trim().isEmpty()
                    && contrasena != null && !contrasena.trim().isEmpty()) {

                PreparedStatement psExiste = conn.prepareStatement(EXISTS_USR);
                psExiste.setInt(1, e.getIdEmpleado());
                ResultSet rs = psExiste.executeQuery();
                rs.next();
                int count = rs.getInt(1);

                if (count > 0) {
                    PreparedStatement psUsr = conn.prepareStatement(UPDATE_USR);
                    psUsr.setString(1, usuario.trim());
                    psUsr.setString(2, contrasena.trim());
                    psUsr.setInt(3, e.getIdEmpleado());
                    psUsr.executeUpdate();
                } else {
                    PreparedStatement psUsr = conn.prepareStatement(INSERT_USR);
                    psUsr.setString(1, usuario.trim());
                    psUsr.setString(2, contrasena.trim());
                    psUsr.setInt(3, e.getIdEmpleado());
                    psUsr.executeUpdate();
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

    /**
     * Elimina el empleado (el usuario se borra en cascada por la FK ON DELETE CASCADE).
     */
    @Override
    public void eliminar(int idEmpleado) throws Exception {
        Connection conn = Conexion.getConexion();
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(DELETE_EMP);
            ps.setInt(1, idEmpleado);
            ps.executeUpdate();
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.close();
        }
    }

    /**
     * Lista todos los empleados con el nombre de usuario si tienen uno.
     */
    @Override
    public List<Empleado> listar() throws Exception {
        List<Empleado> lista = new ArrayList<>();
        Connection conn = Conexion.getConexion();
        PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Empleado emp = new Empleado();
            emp.setIdEmpleado(rs.getInt("id_empleado"));
            emp.setNombre(rs.getString("nombres"));
            emp.setApellido(rs.getString("apellidos"));
            emp.setTelefono(rs.getString("telefono"));
            emp.setCorreo(rs.getString("correo"));
            emp.setDireccion(rs.getString("direccion"));
            // "usuario" viene del LEFT JOIN — puede ser null
            String usr = rs.getString("usuario");
            if (usr != null) {
                Usuario u = new Usuario();
                u.setNombreUsuario(usr);
                emp.setUsuario(u);
            }
            lista.add(emp);
        }
        conn.close();
        return lista;
    }

    /**
     * Busca un empleado por su ID.
     */
    @Override
    public Empleado buscar(int idEmpleado) throws Exception {
        Connection conn = Conexion.getConexion();
        PreparedStatement ps = conn.prepareStatement(SELECT_ID);
        ps.setInt(1, idEmpleado);
        ResultSet rs = ps.executeQuery();
        Empleado emp = null;
        if (rs.next()) {
            emp = new Empleado();
            emp.setIdEmpleado(rs.getInt("id_empleado"));
            emp.setNombre(rs.getString("nombres"));
            emp.setApellido(rs.getString("apellidos"));
            emp.setTelefono(rs.getString("telefono"));
            emp.setCorreo(rs.getString("correo"));
            emp.setDireccion(rs.getString("direccion"));
        }
        conn.close();
        return emp;
    }

    /**
     * Busca el usuario asociado a un empleado.
     */
    @Override
    public Usuario buscarUsuarioPorEmpleado(int idEmpleado) throws Exception {
        Connection conn = Conexion.getConexion();
        PreparedStatement ps = conn.prepareStatement(SELECT_USR_BY_EMP);
        ps.setInt(1, idEmpleado);
        ResultSet rs = ps.executeQuery();
        Usuario u = null;
        if (rs.next()) {
            u = new Usuario();
            u.setIdUsuario(rs.getInt("id_usuario"));
            u.setNombreUsuario(rs.getString("usuario"));
            u.setContrasena(rs.getString("contrasena"));
            u.setIdEmpleado(rs.getInt("id_empleado"));
        }
        conn.close();
        return u;
    }
}
