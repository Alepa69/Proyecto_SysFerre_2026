/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.group.arbolb;

/**
 *
 * @author natha
 */
public class Nodo<T> {

    private T dato;
    private Nodo<T> ramaIzq;
    private Nodo<T> ramaDrch;
    private int alt;

    public Nodo(T dato) {
        this.dato = dato;
        ramaIzq = null;
        ramaDrch = null;
        alt = 0;
    }

    public Nodo(T dato, Nodo<T> ramaIzq, Nodo<T> ramaDrch) {
        this.dato = dato;
        this.ramaIzq = ramaIzq;
        this.ramaDrch = ramaDrch;
        alt = 0;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }

    public void setRamaIzq(Nodo<T> r) {
        this.ramaIzq = r;
    }

    public void setRamaDrch(Nodo<T> r) {
        this.ramaDrch = r;
    }

    public T getDato() {
        return dato;
    }

    public Nodo<T> getRamaIzq() {
        return ramaIzq;
    }

    public Nodo<T> getRamaDrch() {
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
        return "Nodo{dato=" + dato + "}";
    }
}
