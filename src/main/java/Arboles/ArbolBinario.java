package Arboles;

import java.util.ArrayList;

/**
 *
 * @author alfar
 */
public class ArbolBinario <T>{
    private Nodo raiz; //almacenar el primer nodo

    public boolean isEmpty(){
     return raiz == null;
    }
    
    public ArbolBinario() {
    raiz = null; //arbol vacio
    }
    
    public void setRaiz(Nodo raiz) {
        this.raiz = raiz;
    }

    public Nodo getRaiz() {
        return raiz;
    }

    protected ArrayList <T> preOrdenNID(Nodo r, ArrayList a){
    
        if(r != null){
            a.add(r.getDato()); //nodo
            preOrdenNID(r.getRamaIzq(),  a);
            preOrdenNID(r.getRamaDrch(),  a);
        }
        return a;
    }
    
    protected ArrayList <T> inOrdenIND(Nodo r, ArrayList a){ //Metodo Inorden
        if(r != null){
            
            inOrdenIND(r.getRamaIzq(),  a);
            a.add(r.getDato());
            inOrdenIND(r.getRamaDrch(),  a);
        }
        return a;
    }
    
      protected ArrayList <T> postOrdenIDN(Nodo r, ArrayList a){ //Metodo Inorden
        if(r != null){
            
            inOrdenIND(r.getRamaIzq(),  a);
            inOrdenIND(r.getRamaDrch(),  a);
            a.add(r.getDato());
        }
        return a;
    }
      
      public <T extends Comparable> Nodo buscar(T dato){ //devolver el nodo o el dato (en el nodo estan los datos)
          return buscar(dato, raiz); //arbol principal: raiz
      }

      
      private <T extends Comparable> Nodo buscar(T dato, Nodo r){ //devolver el nodo o el dato (en el nodo estan los datos)
          if(r==null){
              
          return null;
          }else if(dato.compareTo(r.getDato()) <  0){
              return buscar(dato, r.getRamaIzq());
          }else if(dato.compareTo(r.getDato()) > 0){
              return buscar(dato, r.getRamaDrch());
          }else{
              return r;
          }
      }
      
      //altura de un arbol
      public int altura(Nodo r){
          if(r == null){
              return 0;
          }else if(isHoja(r)){
              return 1;
          }else{
              int ra = (r.getRamaIzq()== null)?0:altura(r.getRamaIzq());
              int rb = (r.getRamaDrch()== null)?0:altura(r.getRamaDrch());
              
              return 1+Math.max (ra, rb); //devuelva el mayor 
          } 
      }
      
      public boolean isHoja(Nodo r){
          return (r.getRamaIzq()==null) && (r.getRamaDrch()==null);
      }

}

