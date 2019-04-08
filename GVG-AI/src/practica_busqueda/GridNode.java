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

    GridNode(int gCost, int hCost, LinkedList<Types.ACTIONS> actionList,
             Observation position, Orientation orientation, int boulderIndex,
             boolean[][] groundMap, boolean[][] gemsMap, GridNode parent) {
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

    @Override
    public int hashCode() {
        String stringCode = "";
        int hashCode;

        // Add the (X, Y) position
        stringCode += this.position.getX();
        stringCode += this.position.getY();

        stringCode += this.boulderIndex;

        hashCode = Integer.parseInt(stringCode);

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
            && this.boulderIndex == gNode.boulderIndex) {
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