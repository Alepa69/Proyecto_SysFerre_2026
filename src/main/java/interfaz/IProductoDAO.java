
package interfaz;

import java.util.List;

import Arboles.ArbolBusqueda;
import modelo.Producto;

public interface IProductoDAO {

    void insertar(Producto p) throws Exception;

    void actualizar(Producto p) throws Exception;

    void eliminar(int idProducto) throws Exception;

    ArbolBusqueda<Producto> listar() throws Exception; // antes era List<Producto>

    Producto buscar(int idProducto) throws Exception;

}