package practica_busqueda;

import ontology.Types;
import core.game.StateObservation;

import java.util.ArrayList;

class Node extends BaseNode implements Comparable<Node>{
    private int costeG;
    private int costeH;
    private int costeF;
    //private Types.ACTIONS accion;
    private int numAccion;
    private StateObservation estado;
    private ArrayList<Observation> listaGemas;
    //private PlayerObservation jugador;
    private Node padre;

    Node(int costeG, int costeH, Types.ACTIONS accion, int numAccion, StateObservation estado,
         ArrayList<Observation> listaGemas, PlayerObservation jugador, Node padre) {
        super(jugador, accion);
        this.costeG = costeG;
        this.costeH = costeH;
        this.costeF = costeG + costeH;
        this.accion = accion;
        this.numAccion = numAccion;
        this.estado = estado;
        this.listaGemas = listaGemas;
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
            && this.getJugador().equals(node2.getJugador())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Node otherNode) {
        int difCosteF = this.costeF - otherNode.costeF;

        if (difCosteF != 0) {
            return difCosteF;
        }

        int difAccion = this.numAccion - otherNode.numAccion;

        return difAccion;
    }
}
