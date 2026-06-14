
package interfaz;

import Arboles.ArbolAVL;
import java.util.List;

import Arboles.ArbolBusqueda;
import modelo.Producto;

public interface IProductoDAO {

    void insertar(Producto p) throws Exception;

    void actualizar(Producto p) throws Exception;

    void eliminar(int idProducto) throws Exception;

    public ArbolAVL<Producto> listar() throws Exception;

    Producto buscar(int idProducto) throws Exception;

}