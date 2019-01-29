/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica_planificacion;

import ontology.Types;
import core.game.StateObservation;

import java.util.LinkedList;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.PriorityQueue;

/**
 *
 * @author vladislav
 */
public class AStar {
    private Deque<Observation> path;
    private LinkedList<Types.ACTIONS> actions;
    
    public AStar() {
        path = new ArrayDeque<>();
        actions = new LinkedList<>();
    }
}
