/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaz;

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

    List<Cliente> listar() throws Exception;

    Cliente buscar(int idCliente) throws Exception;
}
