package practica_busqueda;

import ontology.Types;


/* Clase para poder comprobar que nodos han sido
   explorados con que acciones
 */
class BaseNode {
    protected PlayerObservation jugador;
    protected Types.ACTIONS accion;

    BaseNode(PlayerObservation jugador, Types.ACTIONS accion) {
        this.jugador = jugador;
        this.accion = accion;
    }

    PlayerObservation getJugador() {
        return this.jugador;
    }

    @Override
    public boolean equals(Object otherBaseNode) {
        if (otherBaseNode == null) {
            return false;
        }

        if (! (otherBaseNode instanceof  BaseNode)) {
            return false;
        }

        BaseNode baseNode2 = (BaseNode) otherBaseNode;

        if (this.jugador.equals(baseNode2.jugador) && this.accion.equals(baseNode2.accion)) {
            return true;
        } else {
            return false;
        }
    }
}
