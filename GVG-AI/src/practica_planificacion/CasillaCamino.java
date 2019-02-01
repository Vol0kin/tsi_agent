/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica_planificacion;

import ontology.Types;
import core.game.StateObservation;

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
