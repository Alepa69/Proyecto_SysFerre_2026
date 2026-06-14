
package interfaz;

import java.util.List;

import Arboles.ArbolBusqueda;
import modelo.Cliente;

/**
 *
 * @author natha
 */

public interface IClienteDAO {

    void insertar(Cliente c) throws Exception;

    void actualizar(Cliente c) throws Exception;

    void eliminar(int idCliente) throws Exception;

    ArbolBusqueda<Cliente> listar() throws Exception;

    Cliente buscar(int idCliente) throws Exception;
}