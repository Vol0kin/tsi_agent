package practica_busqueda;

import ontology.Types;
import core.game.StateObservation;

import java.util.ArrayList;

public class Node implements Comparable<Node>{
    private int costeG;
    private int costeH;
    private int costeF;
    private Types.ACTIONS accion;
    private StateObservation estado;
    private ArrayList<Observation> listaGemas;
    private PlayerObservation jugador;
    private Node padre;

    public Node(int costeG, int costeH, Types.ACTIONS accion, StateObservation estado,
                ArrayList<Observation> listaGemas, PlayerObservation jugador, Node padre) {
        this.costeG = costeG;
        this.costeH = costeH;
        this.costeF = costeG + costeH;
        this.accion = accion;
        this.estado = estado;
        this.listaGemas = listaGemas;
        this.jugador = jugador;
        this.padre = padre;
    }

    public Node getPadre() {
        return padre;
    }

    public StateObservation getEstado() {
        return estado;
    }

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

        if (costeG == node2.costeG && costeH == node2.costeH && costeF == node2.costeF
            && accion.equals(node2.accion) && listaGemas.size() == node2.listaGemas.size()
            && jugador.equals(node2.jugador)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Node otherNode) {
        int difCosteF = this.costeF - otherNode.costeF;

        return difCosteF;
    }
}
