package practica_busqueda;

import ontology.Types;

import java.util.LinkedList;
import java.util.ArrayList;

/**
 *
 * @author vladislav
 */
class PathInformation {
    LinkedList<Types.ACTIONS> plan;
    ArrayList<Observation> listaCasillas;
    int distancia;
    double probabilidadEnemigos;
    
    PathInformation() {
        plan = new LinkedList<>();
        listaCasillas = new ArrayList<>();
        distancia = 0;
        probabilidadEnemigos = 0.0;
    }
}
