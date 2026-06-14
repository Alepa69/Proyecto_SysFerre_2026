/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaz;

import java.util.List;

import Arboles.ArbolBusqueda;
import modelo.Inventario;

/**
 *
 * @author mendo
 */
public interface IInventarioDAO {
    void insertar(Inventario i) throws Exception;

    void actualizar(Inventario i) throws Exception;

    void eliminar(int idInventario) throws Exception;

    List<Inventario> listar() throws Exception;

    Inventario buscar(int idInventario) throws Exception;

    ArbolBusqueda<Inventario> buscarPorNombre(String texto) throws Exception;

    ArbolBusqueda<Inventario> listarOrdenado(String criterio) throws Exception;
}
