package agente; //The package name is the same as the username in the web.

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.SortedMap;
import java.util.TreeMap;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import javafx.util.Pair;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;
import tools.pathfinder.AStar;
import tools.pathfinder.Node;
import tools.pathfinder.PathFinder;

public class Agent extends AbstractPlayer {
    
    // Observation.itype 
    final int wallCode = 0;
    final int groundCode = 1;
    final int houseCode = 5;
    final int waterCode = 6;
    final int burningHouseCode = 8;
    
    protected int cont = 0;
    protected boolean stopPlanning = false;
    protected boolean completed = false;
    protected int searchCode = -1;
    protected Vector2d initialPos;
    protected PathFinder pathfinder;
    protected ArrayList<Vector2d> inaccessibleCells;
    protected ArrayList<Vector2d> waterSources;
    protected Pair<Vector2d, String> goal;
    protected LinkedList<Types.ACTIONS> plan;
    protected SortedMap<Integer, String> burningHousesAtGoal;
    
    protected ArrayList<Vector2d> notExtinguishable;
    protected ArrayList<Vector2d> housesBlocking;
    
    //Constructor. It must return in 1 second maximum.
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        initialPos = getCurrent(so).getKey();
        waterSources = new ArrayList<Vector2d>();
        inaccessibleCells = new ArrayList<Vector2d>();
        goal = new Pair<Vector2d, String>(null, "");
        plan = new LinkedList<Types.ACTIONS>();
        
        ArrayList<Integer> obstacles = new ArrayList<Integer>();
        obstacles.add(houseCode);
        obstacles.add(burningHouseCode);
        obstacles.add(wallCode);
        pathfinder = new PathFinder(obstacles);
        housesBlocking = new ArrayList<Vector2d>();
        
        notExtinguishable = blockedPathIdentifier(so);
        waterSources = scanForObject(so, waterCode);
        setNewPlan(so);
    }
    

    private ArrayList<Vector2d> blockedPathIdentifier(StateObservation stateObs) {
        int currentX;
        int currentY;

        ArrayList<Observation>[][] grid = stateObs.getObservationGrid();
        int contador = 0;
        boolean hayPuerta = false;
        boolean mirado = false;
        boolean entra = false;
        ArrayList<Vector2d> puertas = new ArrayList<Vector2d>();
        // 3, 7
        for(int y = 0; y < grid[0].length-1; ++y) {
        	contador = 0;
        	hayPuerta = false;
            for(int x = 0; x < grid.length-1; ++x){
                for(Observation obs : grid[x][y]) {
                	entra = true;
                	if(!mirado){
	                    if(obs.itype == wallCode) {
	                    	if(hayPuerta){
	                    		Vector2d position = new Vector2d(x-1,y);
	                    		puertas.add(position);
	                    		contador = 0;
	                    		hayPuerta = false;
	                    	}
	                		contador++;
	                    }else{
	                		hayPuerta = false;
	                    	if(contador >= 1)
	                    		hayPuerta = true;
	                    	contador = 0;
	                    }
	                    mirado = true;
                	}
                }
                mirado = false;
                if(!entra){
            		hayPuerta = false;
                	if(contador >= 1)
                		hayPuerta = true;
                	contador = 0;
                }
                entra = false;
            }
        }
                
        ArrayList<Vector2d> casaPuertaBloqueada = new ArrayList<Vector2d>();
        
        for(Vector2d litPos : puertas) {
        	for(Observation obs : grid[(int) litPos.x][(int) litPos.y]) {
                if(obs.itype == houseCode) {
            		Vector2d position = new Vector2d(litPos.x,litPos.y);
                    if(!casaPuertaBloqueada.contains(position)) {
                    	casaPuertaBloqueada.add(position);
                    	housesBlocking.add(position);
                    }
                }
        	}
        	if((int) litPos.y+1 < grid[0].length) {
	    		for(Observation obs : grid[(int) litPos.x][(int) litPos.y+1]) {
	                if(obs.itype == houseCode) {
	            		Vector2d position = new Vector2d(litPos.x,litPos.y+1);
	                    if(!casaPuertaBloqueada.contains(position)) {
	                    	casaPuertaBloqueada.add(position);
	                    	housesBlocking.add(position);
	                    }
	                }
	        	}
        	}
        	if((int) litPos.y-1 >= 0) {
	    		for(Observation obs : grid[(int) litPos.x][(int) litPos.y-1]) {
	                if(obs.itype == houseCode) {
	            		Vector2d position = new Vector2d(litPos.x,litPos.y-1);
	                    if(!casaPuertaBloqueada.contains(position)) {
	                    	casaPuertaBloqueada.add(position);
	                    	housesBlocking.add(position);
	                    }
	                }
	        	}
        	}
        }
        
        ArrayList<Vector2d>  notExtinguishable = new  ArrayList<Vector2d>();
        ArrayList<Vector2d>  casaPuertaBloqueadaClosed = new  ArrayList<Vector2d>();
        
        // Water sources are fixed and unlimited
        
        while(casaPuertaBloqueada.size() > 0) {
        	Vector2d puertaBloqueada = casaPuertaBloqueada.get(0);
        	casaPuertaBloqueadaClosed.add(puertaBloqueada);
        	
        	currentX = (int) puertaBloqueada.x;
        	currentY = (int) puertaBloqueada.y;
        	
    		for(Observation obs : grid[currentX+1][currentY]) {
        		Vector2d position = new Vector2d(currentX+1,currentY);
                if(obs.itype == houseCode) {
                	if(!casaPuertaBloqueadaClosed.contains(position)) {
	                    if(!casaPuertaBloqueada.contains(position)) {
	                    	casaPuertaBloqueada.add(position);
	                    }
                	}
                }else if(obs.itype == burningHouseCode) {
                	notExtinguishable.add(position);
                }
            }
        	
    		for(Observation obs : grid[currentX][currentY+1]) {
        		Vector2d position = new Vector2d(currentX,currentY+1);
                if(obs.itype == houseCode) {
                	if(!casaPuertaBloqueadaClosed.contains(position)) {
	                    if(!casaPuertaBloqueada.contains(position)) {
	                    	casaPuertaBloqueada.add(position);
	                    }
                	}
                }else if(obs.itype == burningHouseCode) {
                	notExtinguishable.add(position);
                }
            }
        	
    		for(Observation obs : grid[currentX-1][currentY]) {
        		Vector2d position = new Vector2d(currentX-1,currentY);
                if(obs.itype == houseCode) {
                	if(!casaPuertaBloqueadaClosed.contains(position)) {
	                    if(!casaPuertaBloqueada.contains(position)) {
	                    	casaPuertaBloqueada.add(position);
	                    }
                	}
                }else if(obs.itype == burningHouseCode) {
                	notExtinguishable.add(position);
                }
            }
        	
    		for(Observation obs : grid[currentX][currentY-1]) {
        		Vector2d position = new Vector2d(currentX,currentY-1);
                if(obs.itype == houseCode) {
                	if(!casaPuertaBloqueadaClosed.contains(position)) {
	                    if(!casaPuertaBloqueada.contains(position)) {
	                    	casaPuertaBloqueada.add(position);
	                    }
                	}
                }else if(obs.itype == burningHouseCode) {
                	notExtinguishable.add(position);
                }
            }
            casaPuertaBloqueada.remove(0);
        }

        ArrayList<Vector2d>  notExtinguishableAdditional = new  ArrayList<Vector2d>();
        
        for(Vector2d pos : notExtinguishable) {
        	currentX = (int) pos.x;
        	currentY = (int) pos.y;
        	boolean burning_houses_right = true;
        	boolean burning_houses_up = true;
        	boolean burning_houses_left = true;
        	boolean burning_houses_down = true;
        	int i = 1;
        	
        	while(burning_houses_right) {
	    		for(Observation obs : grid[currentX+i][currentY]) {
	        		Vector2d position = new Vector2d(currentX+i,currentY);
	                if(obs.itype == burningHouseCode || obs.itype == houseCode) {
	                    if(!notExtinguishable.contains(position)) {
		                    notExtinguishableAdditional.add(position);
	                    }
	                }else{
	                	burning_houses_right = false;
	                }
	            }
	    		i++;
        	}

        	i = 1;
        	while(burning_houses_up) {
	    		for(Observation obs : grid[currentX][currentY+i]) {
	        		Vector2d position = new Vector2d(currentX,currentY+i);
	                if(obs.itype == burningHouseCode || obs.itype == houseCode) {
	                    if(!notExtinguishable.contains(position)) {
		                    notExtinguishableAdditional.add(position);
	                    }
	                }else{
	                	burning_houses_up = false;
	                }
	    		}
	    		i++;
        	}

        	i = 1;
        	while(burning_houses_left) {
	    		for(Observation obs : grid[currentX-i][currentY]) {
	        		Vector2d position = new Vector2d(currentX-i,currentY);
	                if(obs.itype == burningHouseCode || obs.itype == houseCode) {
	                    if(!notExtinguishable.contains(position)) {
		                    notExtinguishableAdditional.add(position);
	                    }
	                }else{
	                	burning_houses_left = false;
	                }
	            }
	    		i++;
        	}

        	i = 1;
        	while(burning_houses_down) {
	    		for(Observation obs : grid[currentX][currentY-i]) {
	        		Vector2d position = new Vector2d(currentX,currentY-i);
	                if(obs.itype == burningHouseCode || obs.itype == houseCode) {
	                    if(!notExtinguishable.contains(position)) {
		                    notExtinguishableAdditional.add(position);
	                    }
	                }else{
	                	burning_houses_down = false;
	                }
	            }
	    		i++;
	    	}
        }
        
    	notExtinguishable.addAll(casaPuertaBloqueadaClosed);
    	notExtinguishable.addAll(notExtinguishableAdditional);
    	
        return notExtinguishable;
    }
    

    //Act function. Called every game step, it must return an action in 40 ms maximum.
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        Types.ACTIONS action = Types.ACTIONS.ACTION_NIL;

        if(completed) {
            if(!stopPlanning) {
                completed = false;
                setNewPlan(stateObs);
            }
        }
        
        if(plan.size() != 0){
            action = plan.pop();
        }
        else {
            completed = true;
        }
        
        return action;
    }
        
    // Sets new plan
    public void setNewPlan(StateObservation stateObs) {
        System.out.println("\nGenerating new plan...");
        
        Pair<Vector2d, String> current = getCurrent(stateObs);
        setGoal(stateObs);
        
        if(!stopPlanning) {            
            // A-star algorithm call to get to goal
            Vector2d startPos = new Vector2d(current.getKey().x, current.getKey().y);
            Vector2d goalPos = new Vector2d(goal.getKey().x, goal.getKey().y);
            
            LinkedList<Types.ACTIONS> pathToGoal = new LinkedList<Types.ACTIONS>();

            // Player is already at goal
            if(!startPos.equals(goalPos)) {
                System.out.println("Path: " + startPos.toString() + " -> " + goalPos.toString());
                pathToGoal = findBestPath(stateObs, startPos, goalPos);
            }
            
            // Player can move to goal
            if(pathToGoal != null) {
                inaccessibleCells.clear();
                
                // Adding optimal orientation
                Types.ACTIONS newAction = Types.ACTIONS.ACTION_NIL;
                switch(goal.getValue()) {
                    case "UP": newAction = Types.ACTIONS.ACTION_UP; break;
                    case "DOWN": newAction = Types.ACTIONS.ACTION_DOWN; break;
                    case "LEFT": newAction = Types.ACTIONS.ACTION_LEFT; break;
                    case "RIGHT": newAction = Types.ACTIONS.ACTION_RIGHT; break;
                }
                if(pathToGoal.isEmpty() || pathToGoal.peekLast() != newAction)
                    pathToGoal.addLast(newAction);
                
                // Add ACTIONS to kill fire at goal
                if(searchCode == burningHouseCode) {
                    for(int splashes = 0; splashes < burningHousesAtGoal.lastKey(); ++splashes) {
                        plan.push(Types.ACTIONS.ACTION_USE);
                    }
                }
                plan.addAll(0, pathToGoal);
                 
                System.out.println("Plan successfully generated");
                System.out.println(plan.toString());
            }
            else {
                if(!inaccessibleCells.contains(goalPos))
                    inaccessibleCells.add(goalPos);
                System.out.println("PATH IS NULL");
                System.out.println("CONTENT OF GOAL: " + stateObs.getObservationGrid()[(int) goalPos.x][(int) goalPos.y]);
            }
        }
    }
    
    // Determines new goal for the player
    private void setGoal(StateObservation stateObs) {
        Vector2d goalPosition = new Vector2d();
        Vector2d intermediatePos = new Vector2d();
        String goalOrientation;
        final int waterKey = 3;
        int minimumWater = 3;
        
        // Player doesn't have enough water
        if(stateObs.getAvatarResources().isEmpty() || (stateObs.getAvatarResources().containsKey(waterKey) && stateObs.getAvatarResources().get(waterKey) < minimumWater)) {
            System.out.println("Searching for water...");
            searchCode = waterCode;
            goalPosition = findNearest(stateObs, waterCode);
        }
        else {
            System.out.println("Searching for burning houses...");
            searchCode =  burningHouseCode;
            intermediatePos = findNearest(stateObs, burningHouseCode);
            goalPosition = intermediatePos;
            ArrayList<Observation>[][] grid = stateObs.getObservationGrid();
            
            // GOAL POSITION CAN'T BE A BURNING HOUSE, WE NEED A SECURE CELL NEARBY
            boolean safe = true;
            boolean contin = true;
            Vector2d pos = new Vector2d();
            // Left side
            for(int left = (int) intermediatePos.x; left >= 0 && contin; --left) {
                pos.x = left;
                pos.y = intermediatePos.y;
                if(!inaccessibleCells.contains(pos)) {
                    safe = true;
                    for(Observation obs : grid[(int) pos.x][(int) pos.y]) {
                        if(obs.itype == burningHouseCode) { 
                            contin = true;
                            safe = false;
                        }
                        else if(obs.itype == houseCode || obs.itype == wallCode){
                            contin = false;
                            safe = false;
                        }
                    }
                }
                if(safe) {
                    goalPosition = pos;
                    contin = false;
                }
            }

            if(!safe) {
                contin = true;
                // Right side
                for(int right = (int) intermediatePos.x; right < grid.length && contin; ++right) {
                    pos.x = right;
                    pos.y = intermediatePos.y;
                    if(!inaccessibleCells.contains(pos)) {
                        safe = true;
                        for(Observation obs : grid[(int) pos.x][(int) pos.y]) {
                            if(obs.itype == burningHouseCode) { 
                                contin = true;
                                safe = false;
                            }
                            else if(obs.itype == houseCode || obs.itype == wallCode){
                                contin = false;
                                safe = false;
                            }
                        }
                    }
                    if(safe) {
                        goalPosition = pos;
                        contin = false;
                    }
                }
            }

            if(!safe) {
                contin = true;
                // Lower side
                for(int down = (int) intermediatePos.y; down < grid[0].length && contin; ++down) {
                    pos.x = intermediatePos.x;
                    pos.y = down;
                    if(!inaccessibleCells.contains(pos)) {
                        safe = true;
                        for(Observation obs : grid[(int) pos.x][(int) pos.y]) {
                            if(obs.itype == burningHouseCode) { 
                                contin = true;
                                safe = false;
                            }
                            else if(obs.itype == houseCode || obs.itype == wallCode){
                                contin = false;
                                safe = false;
                            }
                        }
                    }
                    if(safe) {
                        goalPosition = pos;
                        contin = false;
                    }
                }
            }

            if(!safe) {
                contin = true;
                // Upper side
                for(int up = (int) intermediatePos.y; up >= 0  && contin; --up) {
                    pos.x = intermediatePos.x;
                    pos.y = up;
                    if(!inaccessibleCells.contains(pos)) {
                        safe = true;
                        for(Observation obs : grid[(int) pos.x][(int) pos.y]) {
                            if(obs.itype == burningHouseCode) { 
                                contin = true;
                                safe = false;
                            }
                            else if(obs.itype == houseCode || obs.itype == wallCode){
                                contin = false;
                                safe = false;
                            }
                        }
                    }
                    if(safe) {
                        goalPosition = pos;
                        contin = false;
                    }
                }
            }

            if(safe && !inaccessibleCells.contains(goalPosition)) {
                System.out.println("Cell: " + pos.toString() + " is not in inaccessible list");
                System.out.println("A near safe cell is: " + goalPosition.toString());
            }
            else {
                System.out.println("NOT A SINGLE SAFE SPOT FOUND AROUND TARGET");
                System.out.println("GOAL RESETED TO INITIAL CELL: " + initialPos.toString());
                goalPosition = initialPos;
            }
        }

        if(!stopPlanning) {
            goalOrientation = calculateBestGoalOrientation(stateObs, searchCode, goalPosition);
            System.out.println("Best orientation is " + goalOrientation);
            goal = new Pair<Vector2d, String>(goalPosition, goalOrientation);
        }
    }
    
    // Finds path (actions) to goal using a-star
    private LinkedList<Types.ACTIONS> findBestPath(StateObservation stateObs, Vector2d startPos, Vector2d goalPos){
        LinkedList<Types.ACTIONS> actionPath = new LinkedList<Types.ACTIONS>();
        //pathfinder.run(stateObs);
        pathfinder.state = stateObs;
        pathfinder.grid = stateObs.getObservationGrid();
        pathfinder.astar = new AStar(pathfinder);
        pathfinder.init();
        ArrayList<Node> nodePath = pathfinder.astar.findPath(new Node(startPos),new Node(goalPos));
        
        if(nodePath == null) {
            actionPath = null;
        }
        else {      
            actionPath = convertPath(stateObs, nodePath);
        }
        
        return actionPath;
    }
    
    // Converts AStar module's path to goal to fireman's suitable set of actions to get there
    private LinkedList<Types.ACTIONS> convertPath(StateObservation stateObs, ArrayList<Node> nodePath){
        LinkedList<Types.ACTIONS> actionPath = new LinkedList<Types.ACTIONS>();
        Types.ACTIONS newAction = Types.ACTIONS.ACTION_NIL;
        String orient;
        boolean firstNode = true;
        
        for(Node n : nodePath) {
            orient = getOrientation(n.comingFrom);
            
            switch(orient) {
                case "UP": newAction = Types.ACTIONS.ACTION_UP; break;
                case "DOWN": newAction = Types.ACTIONS.ACTION_DOWN; break;
                case "LEFT": newAction = Types.ACTIONS.ACTION_LEFT; break;
                case "RIGHT": newAction = Types.ACTIONS.ACTION_RIGHT; break;
            }
            
            if(firstNode) {
                firstNode = false;
                
                // if initial orientation doesn't match first action, we need double
                if(getOrientation(stateObs.getAvatarOrientation()) != orient)
                    actionPath.add(newAction);
            }
            // if new action changes orientation, we need double
            else if(actionPath.peekLast() != newAction) {
                actionPath.add(newAction);
            }
            actionPath.add(newAction);
        }
        return actionPath;
    }
    
    // Searches for the nearest object of a given type around player
    private Vector2d findNearest(StateObservation stateObs, final int objectCode) {
        Vector2d currentPos = getCurrent(stateObs).getKey();
        ArrayList<Observation>[][] grid = stateObs.getObservationGrid();
        int currentX = (int) currentPos.x;
        int currentY = (int) currentPos.y;
        int minDist = grid.length * grid[0].length;
        int distToObj = 0;
        Vector2d closest = currentPos;
        
        // Water sources are fixed and unlimited
        if (objectCode == waterCode) {
            for(Vector2d source : waterSources) {
                if(!inaccessibleCells.contains(source)) {
                    distToObj = manhattanDist(currentX, (int) source.x, currentY, (int) source.y);

                    if(minDist > distToObj) {
                        minDist = distToObj;
                        closest = new Vector2d(source.x, source.y);
                    }
                }
            }
        }
        else {
        	ArrayList<Vector2d> burning_houses = scanForObject(stateObs, objectCode);
        	boolean posible = true;
        	for(Vector2d houses : housesBlocking) {
	        	for(Observation observation : grid[(int) houses.x][(int) houses.y]) {
	        		if(observation.itype == houseCode) {
	                    posible = false;
	                }
	            }
        		if(!posible)
        			break;
        	}
        	if(!posible)
        		burning_houses.removeAll(notExtinguishable);
            for(Vector2d pos : burning_houses) {
                if(!inaccessibleCells.contains(pos)) {
                    distToObj = manhattanDist(currentX, (int) pos.x, currentY, (int) pos.y);
                    if(minDist > distToObj) {
                        minDist = distToObj;
                        closest = new Vector2d(pos.x, pos.y);
                    }
                }
            }
        }
        
        if(currentPos != closest) {
            System.out.println("Found it at: " + closest.toString());
        }
        else {
            //stopPlanning = true;
            System.out.println("Not found");
        }
        
        return closest;
    }
    
    // Calculates best orientation for player at goal (facing the most burning houses possible)
    private String calculateBestGoalOrientation(StateObservation stateObs, int searchCode, Vector2d goalPosition) {
        String bestOrientation = "DOWN";

        if(searchCode == burningHouseCode) {
            burningHousesAtGoal = countBurningAroundPos(stateObs, burningHouseCode, goalPosition);
            
            bestOrientation = burningHousesAtGoal.get(burningHousesAtGoal.lastKey());
        }
        
        return bestOrientation;
    }

    // Manhattan distance calculation
    private int manhattanDist(int x0, int x1, int y0, int y1) {
        return Math.abs(x1 - x0) + Math.abs(y1 - y0);
    }
    
    // Returns pair with current position and simplified orientation for player
    private Pair<Vector2d, String> getCurrent(StateObservation stateObs) {
        int blockSize = stateObs.getBlockSize();
        Vector2d pos = new Vector2d(stateObs.getAvatarPosition().x / blockSize, stateObs.getAvatarPosition().y / blockSize);
        return new Pair<Vector2d, String>(pos, getOrientation(stateObs.getAvatarOrientation()));
    }
    
    // Returns a simplified version of orientation (grid)
    private String getOrientation(Vector2d orientation) {
        String simpOrient;
        
        if (Math.abs(orientation.y) > Math.abs(orientation.x)) {
              if (orientation.y < 0) 
                  simpOrient = "UP";
              else 
                  simpOrient = "DOWN";
        } 
        else {
              if (orientation.x > 0) 
                  simpOrient = "RIGHT";
              else 
                  simpOrient = "LEFT";
        }

        return simpOrient;
    }
    
    // Returns ArrayList of positions (grid) of a specific object type
    private ArrayList<Vector2d> scanForObject(StateObservation stateObs, int objectCode){
        ArrayList<Vector2d> objectPositions = new ArrayList<Vector2d>();
        ArrayList<Observation>[][] grid = stateObs.getObservationGrid();
        
        for(int x = 0; x < grid.length; ++x) {
            for(int y = 0; y < grid[0].length; ++y) {
                for(Observation obs : grid[x][y]) {
                    if(obs.itype == objectCode) {
                        objectPositions.add(new Vector2d(x, y));
                    }
                }
            }
        }
        return objectPositions;
    }
    
    // Counts number of (accessible) burning houses NORTH, SOUTH, WEST and EAST of a given position
    private SortedMap<Integer, String> countBurningAroundPos(StateObservation stateObs, int searchCode, Vector2d pos) {
        int goalGridX = (int) (pos.x);
        int goalGridY = (int) (pos.y);
        ArrayList<Observation>[][] grid = stateObs.getObservationGrid();
        boolean stop = false;
        int left_count = 0;
        int right_count = 0;
        int up_count = 0;
        int down_count = 0;
        SortedMap<Integer, String> orientationScores = new TreeMap<Integer, String>();       
       
        // Left side
        for(int left = goalGridX - 1; left >= 0 && !stop; --left) {
            if(grid[left][goalGridY].isEmpty()) {
                stop = true;
            }
            else {
                for(Observation obs : grid[left][goalGridY]) {
                    if(obs.itype == searchCode) {
                        ++left_count;
                    }
                    else if(obs.itype == wallCode || obs.itype == houseCode) {
                        stop = true;
                    }
                }
            }
        }
        orientationScores.put(left_count, "LEFT");
        stop = false;
       
        // Right side
        for(int right = goalGridX + 1; right < grid.length && !stop; ++right) {
            if(grid[right][goalGridY].isEmpty()) {
                stop = true;
            }
            else {
                for(Observation obs : grid[right][goalGridY]) {
                    if(obs.itype == searchCode) {
                        ++right_count;
                    }
                    else if(obs.itype == wallCode || obs.itype == houseCode) {
                        stop = true;
                    }
                }
            }
        }
        orientationScores.put(right_count, "RIGHT");
        stop = false;
       
        // Lower side
        for(int down = goalGridY + 1; down < grid[0].length && !stop; ++down) {
            if(grid[goalGridX][down].isEmpty()) {
                stop = true;
            }
            else {
                for(Observation obs : grid[goalGridX][down]) {
                    if(obs.itype == searchCode) {
                        ++down_count;
                    }
                    else if(obs.itype == wallCode || obs.itype == houseCode) {
                        stop = true;
                    }
                }
            }
        }
        orientationScores.put(down_count, "DOWN");
        stop = false;
       
        // Upper side
        for(int up = goalGridY - 1; up >= 0 && !stop; --up) {
            if(grid[goalGridX][up].isEmpty()) {
                stop = true;
            }
            else {
                for(Observation obs : grid[goalGridX][up]) {
                    if(obs.itype == searchCode) {
                        ++up_count;
                    }
                    else if(obs.itype == wallCode || obs.itype == houseCode) {
                        stop = true;
                    }
                }
            }
        }
        orientationScores.put(up_count, "UP");
       
        System.out.println("Burning buildings at " + pos.toString() + ": " + orientationScores.toString());
        return orientationScores;
    }
}