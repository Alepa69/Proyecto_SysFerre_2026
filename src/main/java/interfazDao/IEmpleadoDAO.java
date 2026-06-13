package interfazDao;

import com.ues.group.arbolb.ArbolB;
import modelo.Empleado;

public interface IEmpleadoDAO {

    void insertar(Empleado empleado) throws Exception;
    void actualizar(Empleado empleado) throws Exception;
    void eliminar(int idEmpleado) throws Exception;
    ArbolB<Empleado> listar() throws Exception;
    Empleado buscar(int idEmpleado) throws Exception;
}
