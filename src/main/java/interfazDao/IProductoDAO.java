/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfazDao;

import java.util.List;
import modelo.Producto;

/**
 *
 * @author natha
 */
public interface IProductoDAO {
    boolean insertar(Producto p);
    boolean actualizar(Producto p);
    boolean eliminar(int idProducto);
    List<Producto> listar();
    Producto buscar(int idProducto);
}
