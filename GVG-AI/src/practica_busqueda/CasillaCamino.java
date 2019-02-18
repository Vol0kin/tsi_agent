package practica_busqueda;

import ontology.Types;

import java.util.LinkedList;

/**
 *
 * @author vladislav
 */
class CasillaCamino {
    int costeG;
    int costeH;
    int costeF;
    Orientation orientacion;
    LinkedList<Types.ACTIONS> acciones;
    Observation observacion;
    CasillaCamino padre;
    
    CasillaCamino(int costeG, int costeH, Orientation orientacion,
                  LinkedList<Types.ACTIONS> acciones, Observation observacion,
                  CasillaCamino padre) {
        this.costeG = costeG;
        this.costeH = costeH;
        this.costeF = costeG + costeH;
        this.orientacion = orientacion;
        this.acciones = acciones;
        this.observacion = observacion;
        this.padre = padre;
    }
}
