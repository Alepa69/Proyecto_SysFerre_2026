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
public class ArbolBusqueda<T extends Comparable<T>> extends ArbolBinario<T> {

    public ArbolBusqueda() {
        super();
    }

    public void insertar(T dato) {
        super.setRaiz(insertar(dato, super.getRaiz()));
    }

    private Nodo<T> insertar(T dato, Nodo<T> r) {
        if (r == null) {
            r = new Nodo<>(dato);
        } else if (dato.compareTo(r.getDato()) < 0) {
            r.setRamaIzq(insertar(dato, r.getRamaIzq()));
        } else if (dato.compareTo(r.getDato()) > 0) {
            r.setRamaDrch(insertar(dato, r.getRamaDrch()));
        } else {
            System.out.println("Duplicado: " + dato);
        }
        return r;
    }

    //Preorden
    public ArrayList<T> NID() {
        ArrayList<T> a = new ArrayList<>();
        return preOrdenNID(super.getRaiz(), a);
    }

    //Inorden
    public ArrayList<T> IND() {
        ArrayList<T> a = new ArrayList<>();
        return inOrdenIND(super.getRaiz(), a);
    }

    // Postorden
    public ArrayList<T> IDN() {
        ArrayList<T> a = new ArrayList<>();
        return postOrdenIDN(super.getRaiz(), a);
    }

    public void quitar(T dato) {
        super.setRaiz(eliminar(dato, super.getRaiz()));
    }

    private Nodo<T> eliminar(T dato, Nodo<T> r) {
        if (r == null) {
            System.out.println("No existe para eliminar: " + dato);
            return null;
        } else if (dato.compareTo(r.getDato()) < 0) {
            r.setRamaIzq(eliminar(dato, r.getRamaIzq()));
        } else if (dato.compareTo(r.getDato()) > 0) {
            r.setRamaDrch(eliminar(dato, r.getRamaDrch()));
        } else {
            // Nodo encontrado
            if (r.getRamaIzq() == null) {
                return r.getRamaDrch();
            } else if (r.getRamaDrch() == null) {
                return r.getRamaIzq();
            } else {
                Nodo<T> sucesor = minimoNodo(r.getRamaDrch());
                r.setDato(sucesor.getDato());
                r.setRamaDrch(eliminar(sucesor.getDato(), r.getRamaDrch()));
            }
        }
        return r;
    }

    private Nodo<T> minimoNodo(Nodo<T> r) {
        while (r.getRamaIzq() != null) {
            r = r.getRamaIzq();
        }
        return r;
    }

    public int contarNodosPadres() {
        return contarNodosPadres(super.getRaiz());
    }

    private int contarNodosPadres(Nodo<T> r) {
        if (r == null || (r.getRamaIzq() == null && r.getRamaDrch() == null)) {
            return 0;
        }
        return 1 + contarNodosPadres(r.getRamaIzq()) + contarNodosPadres(r.getRamaDrch());
    }
}
