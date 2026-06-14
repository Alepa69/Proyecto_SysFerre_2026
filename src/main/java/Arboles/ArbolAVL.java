package Arboles;

import java.util.ArrayList;

public class ArbolAVL<T> extends ArbolBinario {

    int fe; // Factor de equilibrio

    public ArbolAVL() {
        super();
        this.fe = 0;
    }

    public <T extends Comparable> void insertar(T dato) {
        super.setRaiz(insertar(dato, super.getRaiz()));
    }

    public <T extends Comparable> Nodo insertar(T dato, Nodo r) {
        if (r == null) {
            r = new Nodo(dato);
        } else if (dato.compareTo(r.getDato()) < 0) {
            Nodo izd = insertar(dato, r.getRamaIzq());
            r.setRamaIzq(izd);
        } else if (dato.compareTo(r.getDato()) > 0) {
            Nodo drch = insertar(dato, r.getRamaDrch());
            r.setRamaDrch(drch);
        } else {
            System.out.println("Duplicado: " + dato);
        }

        return balance(dato, r);
    }

    private <T extends Comparable> Nodo balance(T dato, Nodo r) {
        if (r != null) {
            if (dato.compareTo(r.getDato()) < 0) {
                balance(dato, r.getRamaIzq());
            } else if (dato.compareTo(r.getDato()) > 0) {
                balance(dato, r.getRamaDrch());
            }

            fe = alturaHijo(r.getRamaDrch()) - alturaHijo(r.getRamaIzq());

            switch (fe) {
                case -2:
                    if (alturaHijo(r.getRamaIzq().getRamaIzq()) > alturaHijo(r.getRamaIzq().getRamaDrch())) {
                        r = RII(r, r.getRamaIzq());
                    } else {
                        r = RID(r, r.getRamaIzq());
                    }
                    break;
                case 2:
                    if (alturaHijo(r.getRamaDrch().getRamaDrch()) > alturaHijo(r.getRamaDrch().getRamaIzq())) {
                        r = RDD(r, r.getRamaDrch());
                    } else {
                        r = RDI(r, r.getRamaDrch());
                    }
                    break;
                default:
                    r = actualizarAlturaHijo(r);
            }
        }
        return r;
    }

    private int alturaHijo(Nodo r) {
        if (r == null) {
            return 0;
        }
        return altura(r);
    }

    private Nodo actualizarAlturaHijo(Nodo r) {
        return r;
    }

    // Rotación simple a la derecha (RII)
    private Nodo RII(Nodo r, Nodo y) {
        r.setRamaIzq(y.getRamaDrch());
        y.setRamaDrch(r);
        return y;
    }

    // Rotación simple a la izquierda (RDD)
    private Nodo RDD(Nodo r, Nodo y) {
        r.setRamaDrch(y.getRamaIzq());
        y.setRamaIzq(r);
        return y;
    }

    // Rotación doble izquierda-derecha (RID)
    private Nodo RID(Nodo r, Nodo y) {
        Nodo z = RDD(y, y.getRamaDrch());
        r.setRamaIzq(z);
        return RII(r, z);
    }

    // Rotación doble derecha-izquierda (RDI)
    private Nodo RDI(Nodo r, Nodo y) {
        Nodo z = RII(y, y.getRamaIzq());
        r.setRamaDrch(z);
        return RDD(r, z);
    }

    public ArrayList IND() {
        ArrayList a = new ArrayList();
        return super.inOrdenIND(super.getRaiz(), a);
    }

    public ArrayList NID() {
        ArrayList a = new ArrayList();
        return super.preOrdenNID(super.getRaiz(), a);
    }

    public ArrayList IDN() {
        ArrayList a = new ArrayList();
        return super.postOrdenIDN(super.getRaiz(), a);
    }
}