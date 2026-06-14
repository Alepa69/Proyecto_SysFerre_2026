package interfaz;

import modelo.Empleado;
import Arboles.ArbolBusqueda; 

public interface IEmpleadoDAO {

    void insertar(Empleado empleado) throws Exception;
    void actualizar(Empleado empleado) throws Exception;
    void eliminar(int idEmpleado) throws Exception;
    ArbolBusqueda<Empleado> listar() throws Exception; 
    Empleado buscar(int idEmpleado) throws Exception;
}