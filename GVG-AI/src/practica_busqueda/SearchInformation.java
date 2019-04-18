package practica_busqueda;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.HashSet;

class SearchInformation {
    private PriorityQueue<GridNode> openList;
    private LinkedList<GridNode> closedList;
    private HashSet<GridNode> exploredList;
    private ArrayList<boolean [][]> boulderConfigurations;
    private boolean isEmpty;
    private int exploredStates;
    final static private int MAX_STATES = 185000;

    SearchInformation() {
        this.isEmpty = true;
        this.exploredStates = 0;
    }

    SearchInformation(PriorityQueue<GridNode> openList, LinkedList<GridNode> closedList, HashSet<GridNode> exploredList,
                      ArrayList<boolean [][]> boulderConfigurations, int exploredStates) {

        this.openList = openList;
        this.closedList = closedList;
        this.exploredList = exploredList;
        this.boulderConfigurations = boulderConfigurations;
        this.isEmpty = false;
        this.exploredStates = exploredStates;
    }


    PriorityQueue<GridNode> getOpenList() { return this.openList; }

    LinkedList<GridNode> getClosedList() { return this.closedList; }

    HashSet<GridNode> getExploredList() { return this.exploredList; }

    ArrayList<boolean [][]> getBoulderConfigurations() { return this.boulderConfigurations; }

    boolean isEmpty() { return this.isEmpty; }

    int getExploredStates() {return this.exploredStates; }

    static int getMaxStates() { return MAX_STATES; }
}
