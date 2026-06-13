/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author natha
 */
public class Proveedor implements Comparable<Proveedor> {

    private int idProveedor;
    private String nombre;
    private String nit;
    private String direccion;
    private String telefonos;
    private String correo;

    public Proveedor() {
    }

    public Proveedor(int idProveedor, String nombre, String nit,
            String direccion, String telefonos, String correo) {
        this.idProveedor = idProveedor;
        this.nombre = nombre;
        this.nit = nit;
        this.direccion = direccion;
        this.telefonos = telefonos;
        this.correo = correo;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public String toString() {
        return nombre;
    }
   
    //ordena por nombre
    @Override
    public int compareTo(Proveedor o) {
        Proveedor actual = this;
        return actual.getNombre().compareTo(o.getNombre());
    }
}
