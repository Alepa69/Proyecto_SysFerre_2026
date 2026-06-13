package interfaz;

import java.util.List;

import Arboles.ArbolBusqueda;
import modelo.DetalleVenta;
import modelo.Venta;

/**
 *
 * @author natha
 */
public interface IVentaDAO {

    void registrarVenta(Venta venta) throws Exception;

    void anularVenta(int idVenta) throws Exception;

    ArbolBusqueda<Venta> listar() throws Exception;

    Venta buscar(int idVenta) throws Exception;

    List<DetalleVenta> listarDetalles(int idVenta) throws Exception;

}
