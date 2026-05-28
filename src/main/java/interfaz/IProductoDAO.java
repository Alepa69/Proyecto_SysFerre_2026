
package interfaz;

import java.util.List;
import modelo.Producto;

public interface IProductoDAO {

    void insertar(Producto p) throws Exception;

    void actualizar(Producto p) throws Exception;

    void eliminar(int idProducto) throws Exception;

    List<Producto> listar() throws Exception;

    Producto buscar(int idProducto) throws Exception;

}