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

    GridNode(int gCost, int hCost, LinkedList<Types.ACTIONS> actionList,
             Observation position, Orientation orientation, int boulderIndex,
             GridNode parent) {
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + fCost;

        this.actionList = actionList;
        this.position = position;
        this.orientation = orientation;
        this.parent = parent;
        this.boulderIndex = boulderIndex;

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
        return "Pos: " + position + " Coste g: " + this.gCost + " Costr h: " + this.hCost;
    }
}
