
package interfazDao;

import com.ues.group.arbolb.ArbolBusqueda;
import java.util.List;
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
