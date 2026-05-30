package Arboles;

/**
 *
 * @author alfar
 */
public class Nodo <T>{
    private T dato;
    private Nodo ramaIzq;
    private Nodo ramaDrch;
    private int alt;
    
    public Nodo(T dato) {
        this.dato = dato;
        ramaIzq = null;
        ramaDrch = null;
        alt = 0;
    }

    public Nodo(T dato, Nodo ramaIzq, Nodo ramaDrch) {
        this.dato = dato;
        this.ramaIzq = ramaIzq;
        this.ramaDrch = ramaDrch;
        alt = 0;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }

    public void setRamaIzq(Nodo ramaIzq) {
        this.ramaIzq = ramaIzq;
    }

    public void setRamaDrch(Nodo ramaDrch) {
        this.ramaDrch = ramaDrch;
    }
    
    public T getDato() {
        return dato;
    }

    public Nodo getRamaIzq() {
        return ramaIzq;
    }

    public Nodo getRamaDrch() {
        return ramaDrch;
    }

    public void setAlt(int alt) {
        this.alt = alt;
    }

    public int getAlt() {
        return alt;
    }
    
    @Override
    public String toString() {
        return "Nodo{" + "dato=" + dato + ", ramaIzq=" + ramaIzq + ", ramaDrch=" + ramaDrch + '}';
    }

}

