
package interfaz;

import java.util.List;

import Arboles.ArbolBusqueda;
import modelo.Proveedor;

public interface IProveedoresDAO {

    void insertar(Proveedor p) throws Exception;

    void actualizar(Proveedor p) throws Exception;

    void eliminar(int idProveedor) throws Exception;

    // List<Proveedor> listar() throws Exception;

    Proveedor buscar(int idProveedor) throws Exception;

    public ArbolBusqueda<Proveedor> listar() throws Exception;
}