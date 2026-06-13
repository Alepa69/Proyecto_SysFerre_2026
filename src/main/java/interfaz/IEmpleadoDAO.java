
package interfaz;

import java.util.List;
import modelo.Empleado;
import modelo.Usuario;

public interface IEmpleadoDAO {
        void insertar(Empleado e, String usuario, String contrasena) throws Exception;

    void actualizar(Empleado e, String usuario, String contrasena) throws Exception;

    void eliminar(int idEmpleado) throws Exception;

    List<Empleado> listar() throws Exception;

    Empleado buscar(int idEmpleado) throws Exception;

    Usuario buscarUsuarioPorEmpleado(int idEmpleado) throws Exception;
}
