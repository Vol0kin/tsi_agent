/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica_planificacion;

import ontology.Types.ACTIONS;
import core.game.StateObservation;

/**
 *
 * @author vladislav
 */
class CasillaCamino {
    int costeG;
    int costeH;
    int costeF;
    ACTIONS accion;
    StateObservation estado;
    CasillaCamino padre;
    
    CasillaCamino(int costeG, int costeH, ACTIONS accion,
                  StateObservation estado, CasillaCamino padre) {
        this.costeG = costeG;
        this.costeH = costeH;
        this.costeF = costeG + costeH;
        this.accion = accion;
        this.estado = estado;
        this.padre = padre;
    }
}
