package practica_busqueda;

import java.util.ArrayList;
import java.util.LinkedList;
import ontology.Types;

class GridNode {
    private int gCost;
    private int hCost;
    private int fCost;
    private LinkedList<Types.ACTIONS> actionList;
    private Observation position;
    private Orientation orientation;
    private GridNode parent;
    private int boulderIndex;
    private boolean[][] groundMap;
    private boolean[][] gemsMap;
    private boolean forbidAboveGrid;
    private int remainingGems;
    private ArrayList<Observation> gemsList;

    GridNode(int gCost, int hCost, LinkedList<Types.ACTIONS> actionList,
             Observation position, Orientation orientation, int boulderIndex,
             boolean[][] groundMap, boolean[][] gemsMap, boolean forbidAboveGrid,
             int remainingGems, ArrayList<Observation> gemsList, GridNode parent) {
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = this.gCost + this.hCost;

        this.actionList = actionList;
        this.position = position;
        this.orientation = orientation;
        this.parent = parent;
        this.boulderIndex = boulderIndex;

        this.groundMap = groundMap;
        this.gemsMap = gemsMap;
        this.forbidAboveGrid = forbidAboveGrid;
        this.remainingGems = remainingGems;

        this.gemsList = gemsList;
    }

    int getfCost() {
        return this.fCost;
    }

    int getgCost() {
        return this.gCost;
    }

    GridNode getParent() {
        return this.parent;
    }

    LinkedList<Types.ACTIONS> getActionList() {
        return this.actionList;
    }

    Orientation getOrientation() {
        return this.orientation;
    }

    Observation getPosition() {
        return this.position;
    }

    int getBoulderIndex() { return this.boulderIndex; }

    boolean[][] getGroundMap() { return this.groundMap;  }

    boolean[][] getGemsMap() { return this.gemsMap;  }

    boolean getForbiAboveGrid() { return this.forbidAboveGrid; }

    int getRemainingGems() { return this.remainingGems; }

    ArrayList<Observation> getGemsList() { return this.gemsList; }

    @Override
    public int hashCode() {
        String stringCode = "";
        int hashCode;
        long hashLong;

        stringCode += this.remainingGems;

        // Add the (X, Y) position
        stringCode += this.position.getX();
        stringCode += this.position.getY();

        stringCode += this.boulderIndex;

        hashLong = Long.parseLong(stringCode);
        hashLong = hashLong % Integer.MAX_VALUE;
        hashCode = (int) hashLong;

        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof GridNode)) {
            return false;
        }

        GridNode gNode = (GridNode) o;

        if (this == gNode) {
            return true;
        }

        if (this.position.getX() == gNode.position.getX()
            && this.position.getY() == gNode.position.getY()
            && this.boulderIndex == gNode.boulderIndex
            && this.remainingGems == gNode.remainingGems) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Pos: " + position + " Coste g: " + this.gCost + " Coste h: " + this.hCost +  " Coste f:" + this.fCost + " Index: " + this.boulderIndex;
    }
}
