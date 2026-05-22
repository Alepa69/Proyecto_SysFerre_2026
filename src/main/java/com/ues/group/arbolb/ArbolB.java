/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.group.arbolb;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mendo
 */
public class ArbolB<T extends Comparable<T>> {

    private PaginaB<T> raiz;
    private final int orden;

    public ArbolB(int orden) {
        if (orden < 2) {
            throw new IllegalArgumentException("El orden debe ser >= 2");
        }
        this.orden = orden;
        this.raiz = null;
    }

    private int maxClaves() {
        return 2 * orden - 1;
    }

    public T buscar(T clave) {
        if (raiz == null) {
            return null;
        }
        return buscarRecursivo(raiz, clave);
    }

    private T buscarRecursivo(PaginaB<T> pagina, T clave) {
        int i = 0;
        while (i < pagina.getClaves().size()
                && clave.compareTo(pagina.getClaves().get(i)) > 0) {
            i++;
        }
        if (i < pagina.getClaves().size()
                && clave.compareTo(pagina.getClaves().get(i)) == 0) {
            return pagina.getClaves().get(i);
        }
        if (pagina.isHoja()) {
            return null;
        }
        return buscarRecursivo(pagina.getHijos().get(i), clave);
    }

    public boolean insertar(T clave) {
        if (buscar(clave) != null) {
            return false; // ya existe
        }
        if (raiz == null) {
            raiz = new PaginaB<>(true);
            raiz.getClaves().add(clave);
            return true;
        }
        if (raiz.getClaves().size() == maxClaves()) {
            PaginaB<T> nuevaRaiz = new PaginaB<>(false);
            nuevaRaiz.getHijos().add(raiz);
            dividirHijo(nuevaRaiz, 0);
            raiz = nuevaRaiz;
        }
        insertarNoLleno(raiz, clave);
        return true;
    }

    private void insertarNoLleno(PaginaB<T> pagina, T clave) {
        int i = pagina.getClaves().size() - 1;

        if (pagina.isHoja()) {
            pagina.getClaves().add(null); // espacio extra
            while (i >= 0 && clave.compareTo(pagina.getClaves().get(i)) < 0) {
                pagina.getClaves().set(i + 1, pagina.getClaves().get(i));
                i--;
            }
            pagina.getClaves().set(i + 1, clave);
        } else {
            while (i >= 0 && clave.compareTo(pagina.getClaves().get(i)) < 0) {
                i--;
            }
            i++;
            if (pagina.getHijos().get(i).getClaves().size() == maxClaves()) {
                dividirHijo(pagina, i);
                if (clave.compareTo(pagina.getClaves().get(i)) > 0) {
                    i++;
                }
            }
            insertarNoLleno(pagina.getHijos().get(i), clave);
        }
    }

    private void dividirHijo(PaginaB<T> padre, int indice) {
        int t = orden;
        PaginaB<T> lleno = padre.getHijos().get(indice);
        PaginaB<T> nueva = new PaginaB<>(lleno.isHoja());

        for (int j = 0; j < t - 1; j++) {
            nueva.getClaves().add(lleno.getClaves().get(j + t));
        }
        if (!lleno.isHoja()) {
            for (int j = 0; j < t; j++) {
                nueva.getHijos().add(lleno.getHijos().get(j + t));
            }
        }

        T claveSube = lleno.getClaves().get(t - 1);

        while (lleno.getClaves().size() > t - 1) {
            lleno.getClaves().remove(lleno.getClaves().size() - 1);
        }
        if (!lleno.isHoja()) {
            while (lleno.getHijos().size() > t) {
                lleno.getHijos().remove(lleno.getHijos().size() - 1);
            }
        }

        padre.getHijos().add(indice + 1, nueva);
        padre.getClaves().add(indice, claveSube);
    }

    public List<T> recorridoEnOrden() {
        List<T> lista = new ArrayList<>();
        if (raiz != null) {
            recorrer(raiz, lista);
        }
        return lista;
    }

    private void recorrer(PaginaB<T> pagina, List<T> lista) {
        int n = pagina.getClaves().size();
        for (int i = 0; i < n; i++) {
            if (!pagina.isHoja()) {
                recorrer(pagina.getHijos().get(i), lista);
            }
            lista.add(pagina.getClaves().get(i));
        }
        if (!pagina.isHoja()) {
            recorrer(pagina.getHijos().get(n), lista);
        }
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public PaginaB<T> getRaiz() {
        return raiz;
    }

    public int getOrden() {
        return orden;
    }
}
