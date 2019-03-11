package practica_busqueda;

import ontology.Types;
import core.game.StateObservation;

import java.util.ArrayList;

class Node implements Comparable<Node>{
    private int costeG;
    private int costeH;
    private int costeF;
    private Types.ACTIONS accion;
    private StateObservation estado;
    private ArrayList<Observation> listaGemas;
    private ArrayList<Observation> listaRocas;
    private PlayerObservation jugador;
    private Node padre;

    Node(int costeG, int costeH, Types.ACTIONS accion, StateObservation estado,
         ArrayList<Observation> listaGemas, ArrayList<Observation> listaRocas, PlayerObservation jugador, Node padre) {
        this.costeG = costeG;
        this.costeH = costeH;
        this.costeF = costeG + costeH;
        this.accion = accion;
        this.jugador = jugador;
        this.estado = estado;
        this.listaGemas = listaGemas;
        this.listaRocas = listaRocas;
        this.padre = padre;
    }

    Node getPadre() {
        return this.padre;
    }

    StateObservation getEstado() {
        return this.estado;
    }

    Types.ACTIONS getAccion() {
        return this.accion;
    }

    PlayerObservation getJugador() {
        return this.jugador;
    }

    int getCosteF() {
        return this.costeF;
    }

    int getCosteG() { return this.costeG; }

    @Override
    public boolean equals(Object otherNode) {
        if (otherNode == null) {
            return false;
        }

        if (!(otherNode instanceof  Node)) {
            return false;
        }

        Node node2 = (Node) otherNode;

        if (node2 == this) {
            return true;
        }

        if (listaGemas.size() == node2.listaGemas.size()
                && this.jugador.equals(node2.jugador)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        String stringCode = "";

        // Add to the code the (x,y) position
        stringCode += this.jugador.getX();
        stringCode += this.jugador.getY();

        // Add to the code the player's orientation
        switch(this.jugador.getOrientation()) {
            case N:
                stringCode += 0;
                break;
            case E:
                stringCode += 1;
                break;
            case S:
                stringCode += 2;
                break;
            case W:
                stringCode += 3;
                break;
        }

        // Add to the code the remaining number of gems
        stringCode += listaGemas.size();
/*
        // Sum x and y position of all the boulders
        int boulderSumX = 0, boulderSumY = 0;

        for (Observation boulder: listaRocas) {
            boulderSumX += boulder.getX();
            boulderSumY += boulder.getY();
        }

        int boulderSum = (boulderSumX + boulderSumY) / 13;

        stringCode += boulderSum;*/

       // System.out.println(stringCode);

        // Parse the String code and return it
        int integerCode = Integer.parseInt(stringCode);

        return integerCode;
    }

    @Override
    public int compareTo(Node otherNode) {
        int difCosteF = this.costeF - otherNode.costeF;

        return difCosteF;
    }

    @Override
    public String toString() {
        return "costeG: " + costeG + " costeH: " + costeH + " costeF: " + costeF + " jugador: " + jugador.toString() + " accion: " + this.accion;
    }
}
