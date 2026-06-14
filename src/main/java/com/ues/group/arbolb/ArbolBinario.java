/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.group.arbolb;

import java.util.ArrayList;

/**
 *
 * @author natha
 */

public class ArbolBinario<T extends Comparable<T>> {

    private Nodo<T> raiz;

    public ArbolBinario() {
        raiz = null;
    }

    public boolean isEmpty() {
        return raiz == null;
    }

    public void setRaiz(Nodo<T> raiz)  { this.raiz = raiz; }
    public Nodo<T> getRaiz()           { return raiz; }

    // NID: Nodo - Izquierda - Derecha (preorden)
    protected ArrayList<T> preOrdenNID(Nodo<T> r, ArrayList<T> a) {
        if (r != null) {
            a.add(r.getDato());
            preOrdenNID(r.getRamaIzq(), a);
            preOrdenNID(r.getRamaDrch(), a);
        }
        return a;
    }

    // IND: Izquierda - Nodo - Derecha (inorden)
    protected ArrayList<T> inOrdenIND(Nodo<T> r, ArrayList<T> a) {
        if (r != null) {
            inOrdenIND(r.getRamaIzq(), a);
            a.add(r.getDato());
            inOrdenIND(r.getRamaDrch(), a);
        }
        return a;
    }

    // IDN: Izquierda - Derecha - Nodo (postorden) — tenía bug: llamaba inOrden dos veces
    protected ArrayList<T> postOrdenIDN(Nodo<T> r, ArrayList<T> a) {
        if (r != null) {
            postOrdenIDN(r.getRamaIzq(), a);   // era inOrdenIND — BUG corregido
            postOrdenIDN(r.getRamaDrch(), a);  // era inOrdenIND — BUG corregido
            a.add(r.getDato());
        }
        return a;
    }

    // Búsqueda pública
    public Nodo<T> buscar(T dato) {
        return buscar(dato, raiz);
    }

    // Búsqueda privada recursiva
    private Nodo<T> buscar(T dato, Nodo<T> r) {
        if (r == null) {
            return null;
        } else if (dato.compareTo(r.getDato()) < 0) {
            return buscar(dato, r.getRamaIzq());
        } else if (dato.compareTo(r.getDato()) > 0) {
            return buscar(dato, r.getRamaDrch());
        } else {
            return r;
        }
    }

    // Altura del árbol
    public int altura(Nodo<T> r) {
        if (r == null)      return 0;
        if (isHoja(r))      return 1;
        int ra = altura(r.getRamaIzq());
        int rb = altura(r.getRamaDrch());
        return 1 + Math.max(ra, rb);
    }

    public boolean isHoja(Nodo<T> r) {
        return r.getRamaIzq() == null && r.getRamaDrch() == null;
    }
}
