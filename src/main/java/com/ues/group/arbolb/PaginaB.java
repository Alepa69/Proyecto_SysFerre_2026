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
public class PaginaB<T extends Comparable<T>> {

    private List<T> claves;
    private List<PaginaB<T>> hijos;
    private boolean hoja;

    public PaginaB(boolean hoja) {
        this.claves = new ArrayList<>();
        this.hijos = new ArrayList<>();
        this.hoja = hoja;
    }

    public List<T> getClaves() {
        return claves;
    }

    public void setClaves(List<T> claves) {
        this.claves = claves;
    }

    public List<PaginaB<T>> getHijos() {
        return hijos;
    }

    public void setHijos(List<PaginaB<T>> hijos) {
        this.hijos = hijos;
    }

    public boolean isHoja() {
        return hoja;
    }

    public void setHoja(boolean hoja) {
        this.hoja = hoja;
    }
}
