package Arboles;

import java.util.ArrayList;

/**
 *
 * @author alfar
 */
public class ArbolBusqueda <T> extends ArbolBinario{
    public ArbolBusqueda() {
        
        super();
        
    }
    
    public <T extends Comparable> void insertar(T dato){
        super.setRaiz(insertar(dato,super.getRaiz()));
    }
    
     public <T extends Comparable> Nodo insertar(T dato, Nodo r){
        if(r == null){  
            r = new Nodo(dato);
        }else if(dato.compareTo(r.getDato()) < 0){
            Nodo izd;
            izd = insertar(dato, r.getRamaIzq());
            r.setRamaIzq(izd);
            //insertar(dato,r.getRamaIzq());
        }else if(dato.compareTo(r.getDato()) > 0){
            Nodo drch;
            drch = insertar(dato, r.getRamaDrch());
            r.setRamaDrch(drch);
            //insertar(dato,r.getRamaDrch());
        }else{
            System.out.println("Duplicado!");
        }
        return r;
    }
    
    public ArrayList NID(){
        
        ArrayList a = new ArrayList();
        return  preOrdenNID(super.getRaiz(), a);
        
    }
    
    public ArrayList IND(){
        ArrayList a = new ArrayList();
        return  inOrdenIND(super.getRaiz(), a);
    }
    
    public ArrayList IDN(){
         ArrayList a = new ArrayList();
        return  postOrdenIDN(super.getRaiz(), a);
    }
    
    public <T extends Comparable> void quitar(T dato){
        super.setRaiz(eliminar(dato,super.getRaiz()));
    }
    
     public <T extends Comparable> Nodo eliminar(T dato, Nodo r){
         
         if(r == null){ //r : parte del arbol //si no hay hoja recorrio todo el arbol
             System.out.println("No existe para eliminar!");
         }else if(dato.compareTo(r.getDato()) < 0){
             Nodo izq;
             izq = eliminar(dato, r.getRamaIzq());
             r.setRamaIzq(izq);
         }else if(dato.compareTo(r.getDato()) > 0){
             Nodo drch;
             drch = eliminar(dato, r.getRamaDrch());
             r.setRamaDrch(drch);
             //eliminar(dato, r.getRamaDrch());
         }else{
             Nodo q;
             q = r;
             
             if(q.getRamaIzq()==null){
                 r=q.getRamaDrch();
             }else if(q.getRamaDrch()==null){
                 r=q.getRamaIzq();
             }else{
                 
                 q=aplicarReglaDosHijos(q);             
             }
             
             q=null; //para eliminar
         }
         
         return r;
     }
    
     private Nodo aplicarReglaDosHijos(Nodo actual){
         Nodo aux,  ant; //nodo auxiliar: para trasladar de nodo en nodo ---- nodo ant: nodo anterior
         ant = actual;
         aux = actual.getRamaIzq(); //buscar mas a la derecha de la rama izquierda
         while(aux.getRamaDrch() != null){ //si hay algo en la rama derecha
             ant = aux; //salva
             aux = aux.getRamaDrch();
         }
         actual.setDato(aux.getDato()); //set para cambiar el valor
         if(ant == actual){
             ant.setRamaIzq(aux.getRamaIzq());
         }else{
             ant.setRamaDrch(aux.getRamaIzq());
         }
         return aux;
     }
     
     // Método público para llamar desde el exterior
    public int contarNodosPadres() {
    return contarNodosPadres(super.getRaiz());
    }

    // Método privado recursivo
    private int contarNodosPadres(Nodo r) {
    // Si el nodo es nulo o es una hoja (no tiene hijos), no es un padre
    if (r == null || (r.getRamaIzq() == null && r.getRamaDrch() == null)) {
        return 0;
    }
    
    // Si llegamos aquí, el nodo actual TIENE al menos un hijo, por lo tanto es padre.
    // Sumamos 1 y seguimos buscando en sus ramas.
    return 1 + contarNodosPadres(r.getRamaIzq()) + contarNodosPadres(r.getRamaDrch());
    }

}

