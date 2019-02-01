/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica_planificacion;

import ontology.Types;
import core.game.StateObservation;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;

/**
 *
 * @author vladislav
 */
class PathInformation {
    LinkedList<Types.ACTIONS> plan;
    ArrayList<Observation> listaCasillas;
    int distancia;
    int numEnemigosCamino;
    
    PathInformation() {
        plan = new LinkedList<>();
        listaCasillas = new ArrayList<>();
        distancia = 0;
        numEnemigosCamino = 0;
    }
}
