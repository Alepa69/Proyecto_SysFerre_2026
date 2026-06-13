/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz;

import java.util.List;
import modelo.DetalleVenta;
import modelo.Venta;

/**
 *
 * @author alexi
 */

public interface IVentaDAO {
     /**
     * @param venta objeto Venta con su lista de detalles ya cargada
     * @throws Exception si ocurre cualquier error de persistencia
     */
    void registrarVenta(Venta venta) throws Exception;
 
    /**
     * 
     * @param idVenta ID de la venta a anular
     * @throws Exception si ocurre cualquier error de persistencia
     */
    void anularVenta(int idVenta) throws Exception;
 
    /**
     * 
     * @return lista de ventas (puede estar vacía, nunca null)
     * @throws Exception si ocurre cualquier error de persistencia
     */
    List<Venta> listar() throws Exception;
 
    /**
     * @param idVenta ID de la venta
     * @return objeto Venta, o null si no existe
     * @throws Exception si ocurre cualquier error de persistencia
     */
    Venta buscar(int idVenta) throws Exception;
 
    /**
     * 
     * @param idVenta ID de la venta
     * @return lista de detalles (puede estar vacía, nunca null)
     * @throws Exception si ocurre cualquier error de persistencia
     */
    List<DetalleVenta> listarDetalles(int idVenta) throws Exception;
}
