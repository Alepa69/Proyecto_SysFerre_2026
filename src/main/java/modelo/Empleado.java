package modelo;

public class Empleado implements Comparable<Empleado> {

    private int idEmpleado;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
    private String direccion;
    private String nombreUsuario;
    private String contrasena;

    public Empleado() {
    }

    public Empleado(int idEmpleado, String nombre, String apellido,
            String telefono, String correo, String direccion) {
        this.idEmpleado = idEmpleado;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
    }

    public Empleado(int idEmpleado, String nombre, String apellido,
            String telefono, String correo, String direccion,
            String nombreUsuario, String contrasena) {
        this(idEmpleado, nombre, apellido, telefono, correo, direccion);
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    @Override
    public String toString() {
        return nombre + " " + apellido;
    }

    @Override
    public int compareTo(Empleado otro) {
        return Integer.compare(this.idEmpleado, otro.idEmpleado);
    }
}
