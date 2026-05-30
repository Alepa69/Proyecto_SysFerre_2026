/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author natha
 */
public class Usuario implements Comparable<Usuario> {

    private int idUsuario;
    private String nombreUsuario;
    private String contrasena;
    private int idEmpleado;
    private Empleado empleado; // objeto relacionado (opcional)

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombreUsuario, String contrasena, int idEmpleado) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.idEmpleado = idEmpleado;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    @Override
    public String toString() {
        return nombreUsuario;
    }

    public boolean validarCredenciales(String usuario, String contrasena) {
        String sql = "SELECT contrasena FROM usuarios WHERE usuario = ?";
        try (java.sql.Connection conn = conexion.Conexion.getConexion();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false; // usuario no existe

                String hashGuardado = rs.getString("contrasena");

                if (hashGuardado.startsWith("$2b$") || hashGuardado.startsWith("$2a$")) {
                    String hashNormalizado = hashGuardado.replace("$2b$", "$2a$");
                    return org.mindrot.jbcrypt.BCrypt.checkpw(contrasena, hashNormalizado);
                } else {
                    return hashGuardado.equals(contrasena);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al validar credenciales: " + e.getMessage());
            return false;
        }
    }

        @Override
    public int compareTo(Usuario o) {
        Usuario actual = this;
        return actual.getNombreUsuario().compareTo(o.getNombreUsuario());
    }

}
