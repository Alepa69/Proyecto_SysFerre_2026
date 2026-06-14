/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfazDao;

import java.util.List;
import modelo.Proveedor;

/**
 *
 * @author natha
 */
public interface IProveedorDAO {
    boolean insertar(Proveedor p);
    boolean actualizar(Proveedor p);
    boolean eliminar(int idProveedor);
    List<Proveedor> listar();
    Proveedor buscar(int idProveedor);
}
