package practica_busqueda;

// Agente de prueba. Plantilla para un agente de este juego.

import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.ListIterator;
import java.util.TreeSet;
import java.util.HashSet;

import tools.pathfinder.*;

public class Agent extends BaseAgent{
    
    private boolean primerTurno = true;
    PathInformation informacionPlan;
    
    int it = 0;
    
    private PathFinder pf;
    private boolean stop;
    
    private HashMap<ArrayList<Observation>, Integer> mapaCircuitos; // Al iniciar 
    
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer){
        super(so, elapsedTimer);
        //informacionPlan = new PathInformation();
        
        // Uso el pathFinder de GVG-AI para encontrar los caminos entre todas
        // las casillas evitando los muros (el resto de casillas supongo que
        // son atravesables)
        
        ArrayList<core.game.Observation>[] obstaculos = so.getImmovablePositions();
        
        ArrayList<Integer> tiposObs = new ArrayList<Integer>();
        
        for (ArrayList<core.game.Observation> obs : obstaculos ){
            tiposObs.add(obs.get(0).obsID);
            //System.out.println(obs.get(0).obsID);
        }
        
        tiposObs . add (( int ) 'o' ) ;
        
        pf = new PathFinder ( tiposObs ) ;
        
        pf.VERBOSE = false;
        
        pf.run(so);
        stop = false;
        
        
        //PlayerObservation jugador = this.getPlayer(so);
        
        //System.out.println(new Vector2d(jugador.getX(), jugador.getY()-2));
        
        /*ArrayList<tools.pathfinder.Node> camino = 
            //pf.getPath(new Vector2d(jugador.getX()-2, jugador.getY()), new Vector2d(jugador.getX()-2, jugador.getY()-4));
            //pf.getPath(new Vector2d(1, 6), new Vector2d(1, 4));
            //pf.getPath(new Vector2d(1, 1), new Vector2d(10, 1));
              pf.getPath(new Vector2d(21, 7), new Vector2d(23, 7));*/
        
        // La primera casilla del camino no la guarda! (solo guarda las casillas por las que pasa)
        
        /*for (int i = 0; i < camino.size(); i++)
            System.out.println(camino.get(i).position);*/
        
        mapaCircuitos = new HashMap<ArrayList<Observation>, Integer>(); // Creo el mapa que usa getHeuristicGems
    }
    
    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        //System.out.println(elapsedTimer.remainingTimeMillis());
        /*
        if (iter == 0){
            
            // Observation Grid
            
            ArrayList<Observation>[][] grid = this.getObservationGrid(stateObs);
        
            for (int y = 0; y < grid[0].length; y++)
                for (int x = 0; x < grid.length; x++)
                    System.out.println("Fil: " + y + " Col: " + x + " Obj: " + 
                            grid[x][y].get(0).getType());
            
            
            // Obtener lista de enemigos
            
            ArrayList<Observation>[] enemies = this.getEnemiesList(stateObs);
        
            for (int i = 0; i < enemies.length; i++)
                for (Observation obs : enemies[i])
                    System.out.println(obs);
            
            
            ArrayList<Observation> bats = this.getBatsList(stateObs);
            ArrayList<Observation> scorpions = this.getScorpionsList(stateObs);
            
            System.out.println("Bats:");
            for (Observation bat : bats)
                System.out.println(bat);
            
            System.out.println("Scorpions:");
            for (Observation scorpion : scorpions)
                System.out.println(scorpion);
                       
             // Gemas
            ArrayList<Observation> gems = this.getGemsList(stateObs);
        
            for (Observation obs : gems)
                System.out.println(obs);
            
            // Wall, Ground, Boulder, Empty
            
            ArrayList<Observation> walls = this.getWallsList(stateObs);
            ArrayList<Observation> groundTiles = this.getGroundTilesList(stateObs);
            ArrayList<Observation> boulders = this.getBouldersList(stateObs);
            ArrayList<Observation> emptyTiles = this.getEmptyTilesList(stateObs);
            
            System.out.println("Muros:");
            for (Observation obs : walls)
                System.out.println(obs);
            
            System.out.println("Casillas con suelo:");
            for (Observation obs : groundTiles)
                System.out.println(obs);
            
            System.out.println("Rocas:");
            for (Observation obs : boulders)
                System.out.println(obs);
            
            System.out.println("Casillas vacías:");
            for (Observation obs : emptyTiles)
                System.out.println(obs);
           
            
            // Salida
           
            Observation exit = this.getExit(stateObs);           
            System.out.println(exit);
            
            // Jugador
           
            PlayerObservation player = this.getPlayer(stateObs);
            System.out.println(player);      
            
            // Número de gemas
                    
            // System.out.println(this.getNumGems(stateObs));
            
            // Distancias y colisiones
            
            PlayerObservation player = this.getPlayer(stateObs);
            Observation enemy = this.getScorpionsList(stateObs).get(0);
            
            System.out.println("Colisionan?: " + player.collides(enemy));
            System.out.println("Distancia Euclídea: " + player.getEuclideanDistance(enemy));
            System.out.println("Distancia Manhattan: " + player.getManhattanDistance(enemy));
            
            
            
            pathFinder(1, 4, stateObs);
            informacionPlan.probabilidadEnemigos = enemyProbability(stateObs);
                  
            System.out.println("Distancia del plan = " + informacionPlan.distancia); // NO ES EL NÚMERO DE ACCIONES!
            System.out.println("Probabilidad enemigos = " + informacionPlan.probabilidadEnemigos*100 + " %");
            
            ArrayList<Observation> lista_casillas = informacionPlan.listaCasillas;
            LinkedList<Types.ACTIONS> acciones = informacionPlan.plan;
            
            for (Observation ob : lista_casillas){
                System.out.println("Casilla: " + ob);
            }
            
            for (Types.ACTIONS act : acciones){
                System.out.println("Acción: " + act);
            }
            
            //System.out.println("Probabilidad de enemigos en el camino: " + informacionPlan.probabilidadEnemigos);
            //System.out.println(elapsedTimer.remainingTimeMillis());
            
            try{
                Thread.sleep(1000);
            }
            catch(InterruptedException e){}
            
            
        iter++;
        }*/

        // Voy de una gema a otra hasta tener 9

        // VER LO QUE PASA SI AL COGER UNA GEMA HAGO QUE PIERDA PORQUE ME QUEDE
        // ENCERRADO O HAGO QUE UNA GEMA QUEDE ENCERRADA!!

        // NO COGE LAS GEMAS "DIFICILES"!!! (AQUELLAS EN LAS QUE HAY QUE DESPEJAR EL CAMINO
        // ANTES DE COGERLAS)

        /*
        ArrayList<Observation> gems = new ArrayList();
        int ind = -1;
        LinkedList<Types.ACTIONS> plan = new LinkedList();

        if (it == 0) {
            informacionPlan = pathExplorer(9, 4, stateObs);
        }

        if (it > 0 && informacionPlan.plan.isEmpty()) {
            ArrayList<Observation> goalGems = new ArrayList<>();
            ArrayList<Observation>[][] grid = this.getObservationGrid(stateObs);
            goalGems.add(grid[5][3].get(0));
            goalGems.add(grid[6][3].get(0));
            goalGems.add(grid[7][3].get(0));
            goalGems.add(grid[6][1].get(0));
            goalGems.add(grid[7][1].get(0));
            //goalGems.add(grid[1][4].get(0));
            informacionPlan = pathExplorer(10, 1, stateObs, goalGems);

            for (Types.ACTIONS action: informacionPlan.plan) {
                System.out.println(action);
            }

            for (Observation obs: informacionPlan.listaCasillas) {
                System.out.println(obs);
            }
        }

        plan = informacionPlan.plan;*/

        /*

        if (plan.size() == 0){
            // Veo si tengo el número suficiente de gemas
            
            if (this.getRemainingGems(stateObs) == 0){
                informacionPlan = pathFinder(this.getExit(stateObs).getX(), this.getExit(stateObs).getY(), stateObs);
                
                plan = informacionPlan.plan;
            }
            else{
                // Obtengo las gemas

                gems = this.getGemsList(stateObs);

                // Veo la gema más cercana al jugador

                PlayerObservation player = this.getPlayer(stateObs);

                int min = 100;
                int i = 0;

                for (Observation ob : gems){
                    if (player.getManhattanDistance(ob) < min){
                        // Si el camino tiene longitud 0 es porque no se puede llegar a la gema!!!!
                        // (puede estar debajo de una roca por ejemplo) -> no tengo esa gema en cuenta
                        informacionPlan = pathFinder(gems.get(i).getX(), gems.get(i).getY(), stateObs);

                        if (informacionPlan.plan.size() > 0){
                            min = player.getManhattanDistance(ob);
                            ind = i;

                            plan = informacionPlan.plan; // Guardo el plan para que no se borre al volver a hacer pathFinder
                        }
                    }

                    i++;
                }
            }
        }

        System.out.println("Tam plan= " + plan.size());
        
        if (ind != -1)
            System.out.println(gems.get(ind));
        
        //Types.ACTIONS action = plan.poll();
        
        // <Clústerización> -> Tarda alrededor de 0.08 ms
        
        if (primerTurno){
            primerTurno = false;
            ArrayList<Cluster> clusters = createClusters(3, stateObs); // Epsilon = 3 es un buen valor

            System.out.println("<<<<<<Número de clusters: " + clusters.size());

            for (int i = 0; i < clusters.size(); i++){
                System.out.println("Cluster: " + i);
                System.out.println("PathLength: " + clusters.get(i).getPathLenght());

                for (int j = 0; j < clusters.get(i).getNumGems(); j++)
                    System.out.println(clusters.get(i).getGem(j));
            }
            
            // Obtengo la distancia entre los clusters
            
            int[][] dist_matrix = this.getClustersDistances(clusters, stateObs);
            
            for (int i = 0; i < dist_matrix.length; i++){
                for (int j = 0; j < dist_matrix.length; j++){
                    System.out.print(dist_matrix[i][j] + "\t");
                }
                System.out.print('\n');
            }
        }*/

        // A partir de la iteración 2, empezando en la 0, tarda menos
/*
        if (it >= 2){
            long t11 = elapsedTimer.elapsedMillis();

            StateObservation estado;

            for (int i = 0; i < 90; i++){
                stateObs.advance(Types.ACTIONS.ACTION_UP);
                estado = stateObs.copy();
            }

            long t12 = elapsedTimer.elapsedMillis();
            long t_total = t12 - t11;

            System.out.println("T total: " + t_total);
        }*/

        /*System.out.println("Prueba de distancias usando getHeuristicDistance: ");
        System.out.println(this.getHeuristicDistance(5, 5, 5, 5));
        System.out.println(this.getHeuristicDistance(0, 0, 5, 0)); //No, porque hay muro
        System.out.println(this.getHeuristicDistance(-2, -2, -5, 100)); //No, porque es una posición inválida
        System.out.println(this.getHeuristicDistance(10, 4, 10, 10));
        System.out.println(this.getHeuristicDistance(3, 3, 10, 4));
        System.out.println(this.getHeuristicDistance(21, 6, 24, 6));*/

        // Veo si funciona bien el método getHeuristicGems
        
        if (it == 0) {
            ArrayList<Cluster> clusters = createClusters(3, stateObs);
            Observation jugador = super.getPlayer(stateObs);
            int dist;

            /*for (int i = 0; i < clusters.size(); i++) {
                dist = getHeuristicGems(jugador.getX(), jugador.getY(), jugador.getX(),
                        jugador.getY(), clusters.get(i).getGems());

                for (int j = 0; j < clusters.get(i).getGems().size(); j++)
                    System.out.println(clusters.get(i).getGem(j));

                System.out.println("Cluster " + i + ": " + dist);
            }*/

            ArrayList<Observation> gemas = clusters.get(3).getGems();
            
            // Veo cuánto tarda el método getHeuristicGems
            double t1 = System.currentTimeMillis();

            for (int i = 0; i < 50000; i++){
                dist = getHeuristicGems(jugador.getX(), jugador.getY(), jugador.getX(),
                        jugador.getY(), gemas);
            }

            double t2 = System.currentTimeMillis();

            System.out.println("Tiempo medio en ejecutar getHeuristicGems: " + ((t2 - t1) / 50000.0) + " ms");
        }

        it++;

        //return plan.pollFirst();
        
        return Types.ACTIONS.ACTION_NIL;

    }
        
    // Usa el pathFinder para obtener una cota inferior (optimista) de la distancia entre
    // dos casillas (atraviesa las rocas pero no los muros)
    // DA EXCEPCION SI LA POSICION INICIAL O FINAL ESTA SOBRE UN MURO!
    
    private int getHeuristicDistance(int xStart, int yStart, int xGoal, int yGoal){
        //System.out.println(xStart + " " + yStart);
        if (xStart == xGoal && yStart == yGoal)
            return 0;
        
        // Obtengo la distancia que me da el pathFinder -> número de casillas del array
        ArrayList<tools.pathfinder.Node> camino = 
                pf.getPath(new Vector2d(xStart, yStart), new Vector2d(xGoal, yGoal));
        
        int distance = camino.size();
        
        // Compruebo si existe camino
        if (distance == 0)
            return -1;
        
        // Como sé que si me tengo que mover tanto en X como en Y voy a tener que dar un
        // giro como mínimo, en ese caso añado uno a la distancia

        if (xStart != xGoal && yStart != yGoal)
            distance++;
        
        return distance;
    }

    /*
     * Sobrecarga del metodo getHeuristicDistance
     * Acepta dos observacions y calcula la distancia entre ellos
     *
     * @param obs1 Observacion inicial
     * @param obs2 Observacion final
     *
     * @return Estimacion heuristica de la distnacia entre las dos observaciones
     */
    private int getHeuristicDistance(Observation obs1, Observation obs2) {
        return getHeuristicDistance(obs1.getX(), obs1.getY(), obs2.getX(), obs2.getY());
    }
    
    private PathInformation pathFinder(int xObjetivo, int yObjetivo, StateObservation stateObs) {
        return (pathFinder(xObjetivo, yObjetivo, stateObs, this.getPlayer(stateObs)));
    }
    
    // Cuando se proporcione una posicion inicial personalizada, se debe asignar una orientacion al personaje
    private PathInformation pathFinder(int xObjetivo, int yObjetivo, StateObservation stateObs, PlayerObservation posInicial ){   
        // Plan a devolver
        PathInformation nuevoPlan = new PathInformation();

        // Cola de nodos abiertos
        PriorityQueue<CasillaCamino> listaAbiertos = new PriorityQueue<>(
                (CasillaCamino c1, CasillaCamino c2) -> c1.costeF - c2.costeF);
        
        // Lista de nodos explorados (cerrados)
        ArrayList<CasillaCamino> listaExplorados = new ArrayList<>();
        
        // Observacion del mapa
        ArrayList<Observation>[][] observacionNivel = this.getObservationGrid(stateObs);
        
        // Observacion actual a explorar
        Observation observacionActual;
        
        // Representa el nodo que se explora a continuacion
        CasillaCamino nodoActual;
        
        final ObservationType muro = ObservationType.WALL,
                              roca = ObservationType.BOULDER;
        
        // Objetivo a encontrar
        final Observation objetivo = observacionNivel[xObjetivo][yObjetivo].get(0);
        
        final int numFilas = observacionNivel.length,
                  numColumnas = observacionNivel[0].length;
        
        // Mapa de casillas exploradas
        boolean[][] mapaExplorado = new boolean[numFilas][numColumnas];
        
        for (int i = 0; i < numFilas; i++) {
            for (int j = 0; j < numColumnas; j++) {
                mapaExplorado[i][j] = false;
            }
        }
        
        boolean objetivoEncontrado = false;
        
        listaAbiertos.add(new CasillaCamino(0, this.getHeuristicDistance(posInicial, objetivo),
                                            posInicial.getOrientation(), null, posInicial, null));
        
        mapaExplorado[posInicial.getX()][posInicial.getY()] = true;
        
        while (!listaAbiertos.isEmpty() && !objetivoEncontrado) {
                       
            nodoActual = listaAbiertos.poll();
            
            observacionActual = nodoActual.observacion;
            
            int xActual = observacionActual.getX(),
                yActual = observacionActual.getY();
            
            if (objetivo.equals(observacionActual)) {
                objetivoEncontrado = true;
            } else {                
                LinkedList<Types.ACTIONS> acciones;
                
                int yArriba = yActual - 1,
                    yAbajo = yActual + 1,
                    xIzquierda = xActual - 1,
                    xDerecha = xActual + 1,
                    yArribaRoca = yArriba - 1 < 0 ? 0 : yArriba - 1,
                    costeHijo;  // Coste para ir al hijo (numero de acciones)
            
                Observation casillaArriba = observacionNivel[xActual][yArriba].get(0),
                            casillaAbajo = observacionNivel[xActual][yAbajo].get(0),
                            casillaDerecha = observacionNivel[xDerecha][yActual].get(0),
                            casillaIzquierda = observacionNivel[xIzquierda][yActual].get(0);
                
                // Descendiente superior
                if (!mapaExplorado[xActual][yArriba] &&
                    !casillaArriba.getType().equals(muro) && !casillaArriba.getType().equals(roca) &&
                    !observacionNivel[xActual][yArribaRoca].get(0).getType().equals(roca)) {
                    
                
                    acciones = new LinkedList<>();
                    acciones.addFirst(Types.ACTIONS.ACTION_UP);
                    costeHijo = nodoActual.costeG + 1;
                    
                    if (!nodoActual.orientacion.equals(Orientation.N)) {
                        acciones.addFirst(Types.ACTIONS.ACTION_UP);
                        costeHijo++;
                    }
                    
                    listaAbiertos.add(new CasillaCamino(costeHijo,
                                                        this.getHeuristicDistance(casillaArriba, objetivo),
                                                        Orientation.N, acciones, casillaArriba, nodoActual));
                }
                
                mapaExplorado[xActual][yArriba] = true;
                
                // Descendiente inferior
                if (!mapaExplorado[xActual][yAbajo] &&
                    !casillaAbajo.getType().equals(muro) && !casillaAbajo.getType().equals(roca)) {
                
                    acciones = new LinkedList<>();
                    acciones.addFirst(Types.ACTIONS.ACTION_DOWN);
                    costeHijo = nodoActual.costeG + 1;
                    
                    if (!nodoActual.orientacion.equals(Orientation.S)) {
                        acciones.addFirst(Types.ACTIONS.ACTION_DOWN);
                        costeHijo++;
                    }
                    
                    listaAbiertos.add(new CasillaCamino(costeHijo,
                                                        this.getHeuristicDistance(casillaAbajo, objetivo),
                                                        Orientation.S, acciones, casillaAbajo, nodoActual));
                }
                
                mapaExplorado[xActual][yAbajo] = true;
                
                // Descendiente izquierdo
                if (!mapaExplorado[xIzquierda][yActual] &&
                    !casillaIzquierda.getType().equals(muro) && !casillaIzquierda.getType().equals(roca) &&
                    !observacionNivel[xIzquierda][yArriba].get(0).getType().equals(roca)) {
                    
                
                    acciones = new LinkedList<>();
                    acciones.addFirst(Types.ACTIONS.ACTION_LEFT);
                    costeHijo = nodoActual.costeG + 1;
                    
                    if (!nodoActual.orientacion.equals(Orientation.W)) {
                        acciones.addFirst(Types.ACTIONS.ACTION_LEFT);
                        costeHijo++;
                    }
                    
                    listaAbiertos.add(new CasillaCamino(costeHijo,
                                                        this.getHeuristicDistance(casillaIzquierda, objetivo),
                                                        Orientation.W, acciones, casillaIzquierda, nodoActual));
                }
                
                mapaExplorado[xIzquierda][yActual] = true;
                
                // Descendiente derecho
                if (!mapaExplorado[xDerecha][yActual] &&
                    !casillaDerecha.getType().equals(muro) && !casillaDerecha.getType().equals(roca) &&
                    !observacionNivel[xDerecha][yArriba].get(0).getType().equals(roca)) {
                    
                
                    acciones = new LinkedList<>();
                    acciones.addFirst(Types.ACTIONS.ACTION_RIGHT);
                    costeHijo = nodoActual.costeG + 1;
                    
                    if (!nodoActual.orientacion.equals(Orientation.E)) {
                        acciones.addFirst(Types.ACTIONS.ACTION_RIGHT);
                        costeHijo++;
                    }
                    
                    listaAbiertos.add(new CasillaCamino(costeHijo,
                                                        this.getHeuristicDistance(casillaDerecha, objetivo),
                                                        Orientation.E, acciones, casillaDerecha, nodoActual));
                }
                
                mapaExplorado[xDerecha][yActual] = true;
                
            }                  
            
            listaExplorados.add(nodoActual);
        }
        
        // Obtener la casilla del objetivo
        CasillaCamino recorrido = listaExplorados.get(listaExplorados.size() - 1);
        
        // Guardar distancia recorrida  y acciones en la informacion del plan
        if (objetivoEncontrado) {
            //System.out.println("Encontrado objetivo");
            //nuevoPlan.distancia = posInicial.getManhattanDistance(objetivo);
        
            while (recorrido.padre != null) {
                // Aniadir casillas recorridas
                nuevoPlan.listaCasillas.add(0, recorrido.observacion);
                
                // Aniadir secuencia de acciones realizadas
                nuevoPlan.plan.addAll(0, recorrido.acciones);
                recorrido = recorrido.padre;
            }
            
            // Aniadir casilla inicial
            nuevoPlan.listaCasillas.add(0, recorrido.observacion);
            nuevoPlan.distancia = nuevoPlan.listaCasillas.size();
        }  
        
        return nuevoPlan;
    }

    private ArrayList<Observation> getNeighbours(Observation currentObs, ArrayList<Observation>[][] grid) {
        ArrayList<Observation> neighbours = new ArrayList<>();
        int x = currentObs.getX(), y = currentObs.getY();

        neighbours.add(grid[x][y - 1].get(0));      // Top neighbour
        neighbours.add(grid[x + 1][y].get(0));      // Right neighbour
        neighbours.add(grid[x][y + 1].get(0));      // Bottom neighbour
        neighbours.add(grid[x - 1][y].get(0));      // Left neighbour

        return neighbours;
    }
/*
    private PathInformation pathFinder2(int xGoal, int yGoal, StateObservation stateObs) {
        PathInformation plan = new PathInformation();
        PriorityQueue<GridNode> openList = new PriorityQueue<>(
                (GridNode n1, GridNode n2) -> n1.getfCost() - n2.getfCost());
        LinkedList<GridNode> closedList = new LinkedList<>();
        HashSet<GridNode> exploredList = new HashSet<>();

        final ObservationType BOULDER = ObservationType.BOULDER,
                              WALL = ObservationType.WALL;

        ArrayList<Observation>[][] grid = this.getObservationGrid(stateObs);
        PlayerObservation playerPos = this.getPlayer(stateObs);
        boolean foundGoal = false;

        GridNode currentNode;
        Observation currentObservation;

        final Types.ACTIONS[] actions = {Types.ACTIONS.ACTION_UP, Types.ACTIONS.ACTION_RIGHT, Types.ACTIONS.ACTION_DOWN, Types.ACTIONS.ACTION_LEFT};
        final Orientation[] orientations = {Orientation.N, Orientation.E, Orientation.S, Orientation.W};
        final Observation goal = grid[xGoal][yGoal].get(0);

        openList.add(new GridNode(0, this.getHeuristicDistance(playerPos, goal),
                                  null, playerPos, playerPos.getOrientation(), 0, null));

        while (!foundGoal && !openList.isEmpty()) {
            currentNode = openList.poll();
            currentObservation = currentNode.getPosition();
            Observation position = currentNode.getPosition();
            System.out.println("Nodo actual: " + currentNode);

            if (position.getX() == xGoal && position.getY() == yGoal) {
                foundGoal = true;
            } else {
                ArrayList <Observation> neighbours = this.getNeighbours(currentObservation, grid);

                for (int i = 0; i < neighbours.size(); i++) {
                    Observation nextGrid = neighbours.get(i);
                    int x = nextGrid.getX(), y = nextGrid.getY();

                    if (!nextGrid.getType().equals(BOULDER) && !nextGrid.getType().equals(WALL)
                            && !grid[x][y - 1].get(0).equals(BOULDER)) {
                        int numberActions = 1;
                        LinkedList<Types.ACTIONS> actionList = new LinkedList<>();
                        actionList.addFirst(actions[i]);

                        if (!currentNode.getOrientation().equals(orientations[i])) {
                            numberActions++;
                            actionList.addFirst(actions[i]);
                        }


                        GridNode node = new GridNode(currentNode.getgCost() + numberActions,
                                this.getHeuristicDistance(nextGrid, goal),
                                actionList, nextGrid, orientations[i],0, currentNode);

                        if (exploredList.add(node)) {
                            System.out.println("Nodo expandido: " + node);
                            openList.add(node);
                        }
                    }
                }
            }

            closedList.addFirst(currentNode);
        }

        GridNode path = closedList.getFirst();

        // Guardar distancia recorrida  y acciones en la informacion del plan
        if (foundGoal) {
            //System.out.println("Encontrado objetivo");
            //nuevoPlan.distancia = posInicial.getManhattanDistance(objetivo);

            while (path.getParent() != null) {
                // Aniadir casillas recorridas
                //plan.listaCasillas.add(0, recorrido.getJugador());

                // Aniadir secuencia de acciones realizadas
                plan.plan.addAll(0, path.getActionList());
                path = path.getParent();
            }

            // Aniadir casilla inicial
            //plan.listaCasillas.add(0, recorrido.getJugador());
            //plan.distancia = plan.listaCasillas.size();
        } else {
            System.out.println("no encontrado");
        }

        return plan;
    }*/

    private PathInformation pathExplorer(int xGoal, int yGoal, StateObservation stateObs) {
        PathInformation plan = new PathInformation();
        PriorityQueue<GridNode> openList = new PriorityQueue<>(
                (GridNode n1, GridNode n2) -> n1.getfCost() - n2.getfCost());
        LinkedList<GridNode> closedList = new LinkedList<>();
        HashSet<GridNode> exploredList = new HashSet<>();

        final ObservationType WALL = ObservationType.WALL;

        ArrayList<Observation>[][] grid = this.getObservationGrid(stateObs);
        PlayerObservation playerPos = this.getPlayer(stateObs);
        boolean foundGoal = false;

        GridNode currentNode;
        Observation currentObservation;

        final Types.ACTIONS[] actions = {Types.ACTIONS.ACTION_UP, Types.ACTIONS.ACTION_RIGHT, Types.ACTIONS.ACTION_DOWN, Types.ACTIONS.ACTION_LEFT};
        final Orientation[] orientations = {Orientation.N, Orientation.E, Orientation.S, Orientation.W};
        final Observation goal = grid[xGoal][yGoal].get(0);


        final int XMAX = grid.length,
                  YMAX = grid[0].length;


        // Boulder map (contains boulders and walls)
        boolean[][] boulderMap = new boolean[XMAX][YMAX];
        ArrayList<Observation> boulders = this.getBouldersList(stateObs);
        ArrayList<Observation> walls = this.getWallsList(stateObs);

        boulders.addAll(walls);

        UtilAlgorithms.initMap(boulderMap, boulders, XMAX, YMAX);

        // Create ArrayList containing boulder configurations
        ArrayList<boolean [][]> boulderConfigurations = new ArrayList<>();
        boulderConfigurations.add(boulderMap);

        // Ground map
        boolean[][] groundMap = new boolean[XMAX][YMAX];
        ArrayList<Observation> groundList = this.getGroundTilesList(stateObs);

        UtilAlgorithms.initMap(groundMap, groundList, XMAX, YMAX);

        // Gems map
        boolean[][] gemsMap = new boolean[XMAX][YMAX];
        ArrayList<Observation> gemsList = this.getGemsList(stateObs);

        UtilAlgorithms.initMap(gemsMap, gemsList, XMAX, YMAX);

        // Add first node
        openList.add(new GridNode(0, this.getHeuristicDistance(playerPos, goal),
                null, playerPos, playerPos.getOrientation(), 0,
                groundMap, gemsMap, false, 0, null, null));

        while (!foundGoal && !openList.isEmpty()) {
            // Get first node
            currentNode = openList.poll();

            // Get current observation, boulder map and ground map
            currentObservation = currentNode.getPosition();
            boolean[][] currentBoulders = boulderConfigurations.get(currentNode.getBoulderIndex());
            boolean[][] currentGround = currentNode.getGroundMap();

            if (currentObservation.getX() == xGoal && currentObservation.getY() == yGoal) {
                foundGoal = true;
            } else {
                // Get list of neighbours
                ArrayList<Observation> neighbours = this.getNeighbours(currentObservation, grid);

                // Iterate over each neighbour
                for (int i = 0; i < neighbours.size(); i++) {
                    // Set next grid to explore and get its position
                    Observation nextGrid = neighbours.get(i);
                    int x = nextGrid.getX(), y = nextGrid.getY();

                    // Skip forbidden grid if it's the north grid
                    if (i == 0 && currentNode.getForbiAboveGrid()) {
                        continue;
                    }

                    // Check if the grid is not a boulder in the current boulder map
                    if (!currentBoulders[x][y]) {
                        int numberActions = 1;
                        int bouldIndx = currentNode.getBoulderIndex();
                        Observation nextPosition = nextGrid;
                        boolean[][] nextGround = new boolean[XMAX][YMAX];
                        boolean forbidAboveGrid = false;

                        // Copy the current ground and set the current grid as not ground
                        UtilAlgorithms.copy2DArray(currentGround, nextGround, XMAX, YMAX);

                        nextGround[x][y] = false;

                        // Add actions
                        LinkedList<Types.ACTIONS> actionList = new LinkedList<>();
                        actionList.addFirst(actions[i]);

                        // Check wether an extra action must be done (a turn)
                        if (!currentNode.getOrientation().equals(orientations[i])) {
                            numberActions++;
                            actionList.addFirst(actions[i]);
                        }

                        // Check wether there's a boulder above the current grid and it's nor a gem
                        // nor a wall
                        if (currentBoulders[x][y - 1] && !grid[x][y-1].get(0).getType().equals(WALL)
                                && !grid[x][y].get(0).getType().equals(ObservationType.GEM)) {

                            // Crete new boulder map and copy its old values
                            boolean[][] newBoulders = new boolean[XMAX][YMAX];
                            UtilAlgorithms.copy2DArray(currentBoulders, newBoulders, XMAX, YMAX);

                            int numberBoulders = 0;
                            int boulderPos = y - 1;
                            int emptyPos = y + 1;

                            /* Find out number of boulders above the current grid
                               and the index of the highest grid containing a boulder*/

                            while (newBoulders[x][boulderPos] && !grid[x][boulderPos].get(0).getType().equals(WALL)) {
                                numberBoulders++;
                                boulderPos--;
                            }

                            // Find out the index of the last empty space
                            while (!nextGround[x][emptyPos] && !grid[x][emptyPos].get(0).getType().equals(WALL)) {
                                emptyPos++;
                            }

                            // Modify the boulder map, moving the boulders
                            for (int j = emptyPos - 1; j > boulderPos; j--) {
                                if (j > emptyPos - 1 - numberBoulders) {
                                    newBoulders[x][j] = true;
                                } else {
                                    newBoulders[x][j] = false;
                                }
                            }

                            // Add the new boulder configuration and update boulder map index
                            boulderConfigurations.add(newBoulders);
                            bouldIndx = boulderConfigurations.indexOf(newBoulders);

                            // Set the next position as the same as now
                            nextPosition = currentNode.getPosition();

                            // Forbid the above grid if the current grid is the above grid
                            // and the agent has mined or if the agent has mined another grid
                            // and hasn't changed its position and the previous grid had forbidden
                            // that movement
                            if ((i == 0) || (nextPosition.equals(currentNode.getPosition()) && currentNode.getForbiAboveGrid())) {
                                forbidAboveGrid = true;
                            }
                        }

                        // Create new grid node
                        GridNode node = new GridNode(currentNode.getgCost() + numberActions,
                                this.getHeuristicDistance(nextPosition, goal),
                                actionList, nextPosition, orientations[i], bouldIndx,
                                nextGround, currentNode.getGemsMap(), forbidAboveGrid, 0, null, currentNode);

                        // Add the node to the explored list
                        if (exploredList.add(node)) {
                            openList.add(node);
                        }
                    }
                }
            }

            // Add the current node to the closed list
            closedList.addFirst(currentNode);
        }

        // Get the last explored grid (goal grid)
        GridNode path = closedList.getFirst();

        // Save the path information
        if (foundGoal) {
            plan = parsePlan(path);
        } else {
            System.out.println("no encontrado");
        }

        return plan;
    }

    private PathInformation pathExplorer(int xGoal, int yGoal, StateObservation stateObs, ArrayList<Observation> goalGems) {
        // Creo el objeto que va a guardar la información para el método getHeuristicGems
        // sobre la distancia de las distintas listas de gemas
        mapaCircuitos.clear();
        
        PathInformation plan = new PathInformation();
        PriorityQueue<GridNode> openList = new PriorityQueue<>(
                (GridNode n1, GridNode n2) -> n1.getfCost() - n2.getfCost());
        LinkedList<GridNode> closedList = new LinkedList<>();
        HashSet<GridNode> exploredList = new HashSet<>();

        final ObservationType WALL = ObservationType.WALL;

        ArrayList<Observation>[][] grid = this.getObservationGrid(stateObs);
        PlayerObservation playerPos = this.getPlayer(stateObs);
        boolean foundGoal = false;

        GridNode currentNode;
        Observation currentObservation;

        final Types.ACTIONS[] actions = {Types.ACTIONS.ACTION_UP, Types.ACTIONS.ACTION_RIGHT, Types.ACTIONS.ACTION_DOWN, Types.ACTIONS.ACTION_LEFT};
        final Orientation[] orientations = {Orientation.N, Orientation.E, Orientation.S, Orientation.W};
        final Observation goal = grid[xGoal][yGoal].get(0);


        final int XMAX = grid.length,
                YMAX = grid[0].length;


        // Boulder map (contains boulders and walls)
        boolean[][] boulderMap = new boolean[XMAX][YMAX];
        ArrayList<Observation> boulders = this.getBouldersList(stateObs);
        ArrayList<Observation> walls = this.getWallsList(stateObs);

        boulders.addAll(walls);

        UtilAlgorithms.initMap(boulderMap, boulders, XMAX, YMAX);

        // Create ArrayList containing boulder configurations
        ArrayList<boolean [][]> boulderConfigurations = new ArrayList<>();
        boulderConfigurations.add(boulderMap);

        // Ground map
        boolean[][] groundMap = new boolean[XMAX][YMAX];
        ArrayList<Observation> groundList = this.getGroundTilesList(stateObs);

        UtilAlgorithms.initMap(groundMap, groundList, XMAX, YMAX);

        // Gems map
        boolean[][] gemsMap = new boolean[XMAX][YMAX];
        ArrayList<Observation> gemsList = this.getGemsList(stateObs);

        UtilAlgorithms.initMap(gemsMap, gemsList, XMAX, YMAX);

        // Add first node
        openList.add(new GridNode(0, this.getHeuristicDistance(playerPos, goal),
                null, playerPos, playerPos.getOrientation(), 0,
                groundMap, gemsMap, false, goalGems.size(), goalGems, null));

        while (!foundGoal && !openList.isEmpty()) {
            // Get first node
            currentNode = openList.poll();

            // Get current observation, boulder map and ground map
            currentObservation = currentNode.getPosition();
            boolean[][] currentBoulders = boulderConfigurations.get(currentNode.getBoulderIndex());
            boolean[][] currentGround = currentNode.getGroundMap();
            boolean[][] currentGems = currentNode.getGemsMap();

            int remainingGems = currentNode.getRemainingGems();
            ArrayList<Observation> currentGemsList = currentNode.getGemsList();

            if (currentObservation.getX() == xGoal && currentObservation.getY() == yGoal && remainingGems == 0) {
                foundGoal = true;
            } else if (currentObservation.getX() == xGoal && currentObservation.getY() == yGoal && remainingGems != 0) {
                closedList.addFirst(currentNode);
                continue;
            } else {
                // Get list of neighbours
                ArrayList<Observation> neighbours = this.getNeighbours(currentObservation, grid);

                // Iterate over each neighbour
                for (int i = 0; i < neighbours.size(); i++) {
                    // Set next grid to explore and get its position
                    Observation nextGrid = neighbours.get(i);
                    int x = nextGrid.getX(), y = nextGrid.getY();

                    // Skip forbidden grid if it's the north grid
                    if (i == 0 && currentNode.getForbiAboveGrid()) {
                        continue;
                    }

                    // Check if the grid is not a boulder in the current boulder map
                    if (!currentBoulders[x][y]) {
                        int numberActions = 1;
                        int bouldIndx = currentNode.getBoulderIndex();
                        Observation nextPosition = nextGrid;
                        boolean[][] nextGround = new boolean[XMAX][YMAX];
                        boolean forbidAboveGrid = false;
                        int nextRemainingGems = remainingGems;
                        ArrayList<Observation> nextGemsList = (ArrayList<Observation>) currentGemsList.clone();
                        boolean[][] nextGemsMap = new boolean[XMAX][YMAX];

                        // Copy the current ground and set the current grid as not ground
                        UtilAlgorithms.copy2DArray(currentGround, nextGround, XMAX, YMAX);
                        UtilAlgorithms.copy2DArray(currentGems, nextGemsMap, XMAX, YMAX);

                        nextGround[x][y] = false;

                        // Add actions
                        LinkedList<Types.ACTIONS> actionList = new LinkedList<>();
                        actionList.addFirst(actions[i]);

                        // Check wether an extra action must be done (a turn)
                        if (!currentNode.getOrientation().equals(orientations[i])) {
                            numberActions++;
                            actionList.addFirst(actions[i]);
                        }

                        if (currentGems[x][y] && nextGemsList.contains(nextGrid)) {
                                nextGemsList.remove(nextGemsList.indexOf(nextGrid));
                                nextGemsMap[x][y] = false;
                                nextRemainingGems--;

                        }

                        // Check wether there's a boulder above the current grid and it's nor a gem
                        // nor a wall
                        if (currentBoulders[x][y - 1] && !grid[x][y-1].get(0).getType().equals(WALL)) {

                            // Crete new boulder map and copy its old values
                            boolean[][] newBoulders = new boolean[XMAX][YMAX];
                            UtilAlgorithms.copy2DArray(currentBoulders, newBoulders, XMAX, YMAX);

                            int numberBoulders = 0;
                            int boulderPos = y - 1;
                            int emptyPos = y + 1;

                            /* Find out number of boulders above the current grid
                               and the index of the highest grid containing a boulder
                             */
                            while (newBoulders[x][boulderPos] && !grid[x][boulderPos].get(0).getType().equals(WALL)) {
                                numberBoulders++;
                                boulderPos--;
                            }

                            // Find out the index of the last empty space
                            while (!nextGround[x][emptyPos] && !grid[x][emptyPos].get(0).getType().equals(WALL)) {
                                emptyPos++;
                            }

                            // Modify the boulder map, moving the boulders
                            for (int j = emptyPos - 1; j > boulderPos; j--) {
                                if (j > emptyPos - 1 - numberBoulders) {
                                    newBoulders[x][j] = true;
                                } else {
                                    newBoulders[x][j] = false;
                                }
                            }

                            // Add the new boulder configuration and update boulder map index
                            boulderConfigurations.add(newBoulders);
                            bouldIndx = boulderConfigurations.indexOf(newBoulders);

                            // Set the next position as the same as now
                            nextPosition = currentNode.getPosition();

                            // Forbid the above grid if the current grid is the above grid
                            // and the agent has mined or if the agent has mined another grid
                            // and hasn't changed its position and the previous grid had forbidden
                            // that movement
                            if ((i == 0) || (nextPosition.equals(currentNode.getPosition()) && currentNode.getForbiAboveGrid())) {
                                forbidAboveGrid = true;
                            }

                        }

                        int heuristic;

                        if (nextRemainingGems > 0) {
                            heuristic = this.getHeuristicGems(nextPosition, goal, nextGemsList);
                        } else {
                            heuristic = this.getHeuristicDistance(nextPosition, goal);
                        }

                        // Create new grid node
                        GridNode node = new GridNode(currentNode.getgCost() + numberActions,
                                heuristic,
                                actionList, nextPosition, orientations[i], bouldIndx,
                                nextGround, nextGemsMap, forbidAboveGrid, nextRemainingGems, nextGemsList, currentNode);

                        // Add the node to the explored list
                        if (exploredList.add(node)) {
                            openList.add(node);
                        }
                    }
                }
            }

            // Add the current node to the closed list
            closedList.addFirst(currentNode);
        }



        // Save the path information
        if (foundGoal) {
            // Get the last explored grid (goal grid)
            GridNode path = closedList.getFirst();
            plan = parsePlan(path);
        } else {
            System.out.println("no encontrado");
        }

        return plan;
    }

    /* Funcion que parsea una sucesion de casillas y devuelve informacion
       del plan.

       @param gridPath: Objeto de la clase GridNode desde el que se empieza
              a parsear el camino

       @return Devuelve un nuevo plan, conteniendo la lista de acciones,
               las casillas por las que se pasan y la distancia recorrida.
     */
    private PathInformation parsePlan(GridNode gridPath) {
        PathInformation plan = new PathInformation();

        while (gridPath.getParent() != null) {
            plan.plan.addAll(0, gridPath.getActionList());
            plan.listaCasillas.add(0, gridPath.getPosition());
            plan.distancia++;

            gridPath = gridPath.getParent();
        }

        plan.listaCasillas.add(0, gridPath.getPosition());
        plan.distancia++;

        return plan;
    }

    private PathInformation stateExplorer(int xGoal, int yGoal, StateObservation stateObs) {
        PathInformation plan = new PathInformation();
        PriorityQueue<Node> listaAbiertos = new PriorityQueue<>(
                (Node n1, Node n2) -> n1.getCosteF() - n2.getCosteF());
        ArrayList<Node> listaCerrados = new ArrayList<>();
        HashSet<Node> listaExplorados = new HashSet<>();

        final ObservationType ROCA = ObservationType.BOULDER,
                              MURO = ObservationType.WALL;

        final int NUM_ACCIONES = 5,
                ARRIBA = 0,
                DERECHA = 1,
                ABAJO = 2,
                IZQUIERDA = 3;

        final Types.ACTIONS[] listaAcciones = {Types.ACTIONS.ACTION_UP, Types.ACTIONS.ACTION_RIGHT,
                Types.ACTIONS.ACTION_DOWN, Types.ACTIONS.ACTION_LEFT};


        Node nodoActual, nodoSucesor;
        boolean encontradoObjetivo = false;
        PlayerObservation posJugador = this.getPlayer(stateObs);
        ArrayList<Observation>[][] observacion = this.getObservationGrid(stateObs);

        boolean cogerGema = observacion[xGoal][yGoal].get(0).getType().equals(ObservationType.GEM);

        nodoSucesor = new Node(0,
                                posJugador.getManhattanDistance(observacion[xGoal][yGoal].get(0)),
                         null,
                                stateObs,
                                this.getGemsList(stateObs),
                                this.getBouldersList(stateObs),
                                posJugador,
                         null);

        listaAbiertos.add(nodoSucesor);

        while (!encontradoObjetivo && !listaAbiertos.isEmpty()) {
            nodoActual = listaAbiertos.poll();
            PlayerObservation jugador = nodoActual.getJugador();
            StateObservation estadoObservacion = nodoActual.getEstado();
            observacion = this.getObservationGrid(estadoObservacion);

            //System.out.println(nodoActual);

            if (cogerGema && ((jugador.getX() == xGoal && jugador.getY() == yGoal) ||
                  !observacion[xGoal][yGoal].get(0).getType().equals(ObservationType.GEM))) {
                encontradoObjetivo = true;
            } else if (!cogerGema && jugador.getX() == xGoal && jugador.getY() == yGoal){
                encontradoObjetivo = true;
            } else {

                int xActual = jugador.getX(), yActual = jugador.getY();

                // Comprobar que acciones pueden ser aplicadas para que casillas
/*
                // Comprobar casilla de arriba
                if (!observacion[xActual][yActual - 1].get(0).getType().equals(ROCA)
                        && !observacion[xActual][yActual - 1].get(0).getType().equals(MURO)) {
                    accionesAplicables[ARRIBA] = true;

                    if (posJugador.getOrientation().equals(Orientation.N)) {
                        accionesAplicables[PICAR] = true;
                    }
                }

                // Comprobar casilla a la derecha
                if (!observacion[xActual + 1][yActual].get(0).getType().equals(ROCA)
                        && !observacion[xActual + 1][yActual].get(0).getType().equals(MURO)) {
                    accionesAplicables[DERECHA] = true;

                    if (posJugador.getOrientation().equals(Orientation.E)) {
                        accionesAplicables[PICAR] = true;
                    }
                }

                // Comprobar casilla de abajo
                if (!observacion[xActual][yActual + 1].get(0).getType().equals(ROCA)
                        && !observacion[xActual][yActual + 1].get(0).getType().equals(MURO)) {
                    accionesAplicables[ABAJO] = true;

                    if (posJugador.getOrientation().equals(Orientation.S)) {
                        accionesAplicables[PICAR] = true;
                    }
                }

                // Comprobar casilla a la izquierda
                if (!observacion[xActual - 1][yActual].get(0).getType().equals(ROCA)
                        && !observacion[xActual - 1][yActual].get(0).getType().equals(MURO)) {
                    accionesAplicables[IZQUIERDA] = true;

                    if (posJugador.getOrientation().equals(Orientation.W)) {
                        accionesAplicables[PICAR] = true;
                    }
                }*/

                for (int i = 0; i < 4; i++) {
                    StateObservation forwardState = estadoObservacion.copy();
                    forwardState.advance(listaAcciones[i]);

                    PlayerObservation nuevaPosJugador = this.getPlayer(forwardState);
                    // System.out.println("\t Pos jugador: " + nuevaPosJugador);

                    observacion = this.getObservationGrid(forwardState);
                    if (!nuevaPosJugador.hasDied()) {
                        nodoSucesor = new Node(nodoActual.getCosteG() + 1,
                                                    this.getHeuristicDistance(nuevaPosJugador, observacion[xGoal][yGoal].get(0)),
                                                    listaAcciones[i],
                                                    forwardState,
                                                    this.getGemsList(forwardState),
                                                    this.getBouldersList(forwardState),
                                                    nuevaPosJugador,
                                                    nodoActual);
                        //System.out.println(nodoSucesor);
                        // Comprobar si para una posicion y una accion no se ha explorado antes ese nodo
                        if (listaExplorados.add(nodoSucesor)) {
                            // System.out.println("\taccion: " + listaAcciones[i]);
                            listaAbiertos.add(nodoSucesor);
                        }
                    }
                }
            }

            listaCerrados.add(nodoActual);
        }


        // Obtener la casilla del objetivo
        Node recorrido = listaCerrados.get(listaCerrados.size() - 1);

        // Guardar distancia recorrida  y acciones en la informacion del plan
        if (encontradoObjetivo) {
            //System.out.println("Encontrado objetivo");
            //nuevoPlan.distancia = posInicial.getManhattanDistance(objetivo);

            while (recorrido.getPadre() != null) {
                // Aniadir casillas recorridas
                plan.listaCasillas.add(0, recorrido.getJugador());

                // Aniadir secuencia de acciones realizadas
                plan.plan.addFirst(recorrido.getAccion());
                recorrido = recorrido.getPadre();
            }

            // Aniadir casilla inicial
            plan.listaCasillas.add(0, recorrido.getJugador());
            plan.distancia = plan.listaCasillas.size();
        } else {
            System.out.println("no encontrado");
        }


        return plan;
    }
    
    private double enemyProbability(PathInformation plan, StateObservation stateObs) {
        // Asociar pares enemigo:probabilidad
        Map<Observation, Double> probabilidadesEnemigos = new HashMap<>();
        
        ArrayList<Observation> enemigos = new ArrayList<>();
        ArrayList<Observation>[][] observacionNivel = this.getObservationGrid(stateObs);
        Deque<Observation> casillasLibres = new ArrayDeque<>();
        
        // Numero de enemigos encontrados en el camino
        int numEnemigosCamino = 0;
        
        /* Posibles casillas a las que puede llegar un enemigo.
        Se resetean para cada enemigo */
        ArrayList<Observation> posiblesCasillasCamino = new ArrayList<>();
        
        // Probabilidad acumulada de encontrar enemigos en el camino
        // Probabilidad total de encontrarse con algun enemigo por el camino
        double probabilidadAcumulada = 1.0,
               probabilidadTotal = 0.0;
        
        enemigos.addAll(this.getBatsList(stateObs));
        enemigos.addAll(this.getScorpionsList(stateObs));
        
        Observation observacionActual;
        
        final int numFilas = observacionNivel.length,
                  numColumnas = observacionNivel[0].length,
                  numEnemigos = enemigos.size();
        
        final ObservationType muro = ObservationType.WALL,
                              vacio = ObservationType.EMPTY;
        
        /* Probabilidad del enemigo de moverse en una determinada direccion.
        4 posibles direcciones: arriba, abajo, izquierda y derecha */
        final double probMovimientoDireccion = 0.25;
        
        // Mapa de celdas exploradas para cada enemigo
        boolean [][] celdasExploradas = new boolean[numFilas][numColumnas];
        
        for (int i = 0; i < numFilas; i++) {
            for (int j = 0; j < numColumnas; j++) {
                celdasExploradas[i][j] = false;
            }
        }
        
        // Obtener para cada enemigo las posibles casillas a las que puede llegar
        for (int i = 0; i < numEnemigos; i++) {
            Observation enemigoActual = enemigos.get(i);
            casillasLibres.addFirst(enemigoActual);            
            
            boolean[][] mapaPropio = celdasExploradas.clone();            
            mapaPropio[enemigos.get(i).getX()][enemigos.get(i).getY()] = true;  
            
            ArrayList<Observation> casillasHijo = new ArrayList<>();
            
            // Obtener las casillas adyacentes a las que puede llegar el enemigo
            while (!casillasLibres.isEmpty()) {
                observacionActual = casillasLibres.pollFirst();
                
                int xActual = observacionActual.getX(),
                    yActual = observacionActual.getY();
                
                casillasHijo.clear();
                
                casillasHijo.add(observacionNivel[xActual][yActual - 1].get(0));     // casilla arriba
                casillasHijo.add(observacionNivel[xActual][yActual + 1].get(0));     // casilla abajo
                casillasHijo.add(observacionNivel[xActual - 1][yActual].get(0));     // casilla izquierda
                casillasHijo.add(observacionNivel[xActual + 1][yActual].get(0));     // casilla derecha
                
                for (Observation obs: casillasHijo) {
                    xActual = obs.getX();
                    yActual = obs.getY();
                    
                    if (!mapaPropio[xActual][yActual]) {
                        
                        if (obs.getType().equals(vacio)) {
                            casillasLibres.addLast(obs);
                        }
                        
                        if (!obs.getType().equals(muro)) {
                            posiblesCasillasCamino.add(obs);
                        }                        
                    }
                    
                    mapaPropio[xActual][yActual] = true;
                }                
            }
            
            /* Iterar sobre las posibles casillas para comprobar cuales
            se encuentran dentro del plan del agente. Eliminar aquellas
            que no se encuentren en el camino del agente */
            ListIterator iterator = posiblesCasillasCamino.listIterator();
            
            while (iterator.hasNext()) {
                Observation casilla = (Observation) iterator.next();
                boolean contenidaCasillaCamino = false;
                
                for (Observation casillaCamino: plan.listaCasillas) {
                    if (casillaCamino.equals(casilla)) {
                        contenidaCasillaCamino = true;
                    }
                }
                
                if (!contenidaCasillaCamino) {
                    iterator.remove();
                }
            }
            
            
            // Obtener la probabilidad de que el enemigo se encuentre en el camino
            double probabilidadEnemigo = 0.0;
            
            /* Obtener la distancia a la casilla más cercana a la del enemigo
            La distancia tiene que ser menor a la raiz de la distancia del camino
            del agente para que pueda ser alcanzable */
            if (!posiblesCasillasCamino.isEmpty()) {
                numEnemigosCamino++;
                int distanciaMinima = Integer.MAX_VALUE;
                
                for (Observation casilla : posiblesCasillasCamino) {
                    int distanciaCasilla = enemigoActual.getManhattanDistance(casilla);
                    
                    if (distanciaCasilla < distanciaMinima) {
                        distanciaMinima = distanciaCasilla;
                    }
                }
                
                /* La probabilidad es 1/4 (numero de posibles casillas a las 
                que moverse en cada momento) elevado a la distancia hasta la
                casilla más próxima */
                if (distanciaMinima < Math.sqrt(plan.distancia)) {
                    probabilidadEnemigo = Math.pow(probMovimientoDireccion, distanciaMinima);
                }
            }
            
            probabilidadesEnemigos.put(enemigoActual, probabilidadEnemigo);         
            posiblesCasillasCamino.clear();
        }
        
        /* Obtener la probabilidad de que no se cruce ningun enemigo en el camino
        (1 - probabilidadInidividual) y realizar la multiplicacion de estas para
        obtener la interseccion.
        La probabilidadTotal sera 1 - probabilidadAcumulada (la probabilidad de 
        que no haya ningun enemigo por el camino)
        */
        if (numEnemigosCamino > 0) {
            for (double probabilidadInidividual: probabilidadesEnemigos.values()) {
                probabilidadAcumulada *= 1.0 - probabilidadInidividual;
            }
            
            probabilidadTotal = 1.0 - probabilidadAcumulada;
        }
        
        return probabilidadTotal;
    }
    
    // <Clústerización> -> Tarda alrededor de 0.08 ms
    // Uso el algoritmo DBSCAN para clasificar las gemas en clústeres (grupos)
    // Aquellas gemas clasificadas como ruido las añado a un clúster en el que solo hay una gema
    // epsilon: radio (en distancia Manhattan) en el que se buscan gemas para el mismo clúster
    private ArrayList<Cluster> createClusters(int epsilon, StateObservation stateObs){
        ArrayList<Observation> gems = this.getGemsList(stateObs);
        int gems_size = gems.size();
        boolean[] visited = new boolean[gems_size]; // Valor inicial -> false
        int[] ind_cluster = new int[gems_size]; // Indice del clúster al que pertenece la gema -> -1 si no pertenece a ningún clúster todavía
        
        for (int i = 0; i < ind_cluster.length; i++)
            ind_cluster[i] = -1;
       
        int num_cluster = -1;
        Observation this_gem;
        
        for (int i = 0; i < gems_size && !visited[i]; i++){
            this_gem = gems.get(i);
            
            if (ind_cluster[i] == -1){ // No ha sido visitada pero todavía no pertenece a ningún clúster -> creo un nuevo clúster y la añado
                num_cluster++;
                ind_cluster[i] = num_cluster; // La gema pertenece a ese clúster  
            }
            // Añado al clúster de la gema las gemas del vecindario
            
            for (int j = 0; j < gems_size; j++){ // Tengo que recorrer también las gemas ya visitadas para que funcione bien!
                
                if (this_gem.getManhattanDistance(gems.get(j)) <= epsilon) // Esa gema es del vecindario -> la añado al clúster
                    ind_cluster[j] = ind_cluster[i];
            }
                
            visited[i] = true;
        }
        
        ArrayList<Cluster> clusters = new ArrayList();
        
        for (int i = 0; i <= num_cluster; i++)
            clusters.add(new Cluster());
        
        // Añado cada gema a su clúster correspondiente
        for (int i = 0; i < gems_size; i++)
            clusters.get(ind_cluster[i]).addGem(gems.get(i));
   
        // Calculo el pathLength de cada clúster
        for (int i = 0; i <= num_cluster; i++)
            clusters.get(i).calculatePathLength();
        
        return clusters;
    }

    // Obtiene las distancias aproximadas entre cada pareja de clusters usando una matriz de distancias
    // Se calcula primero para cada pareja de clusters las dos gemas más cercanas
    // entre sí usando la distancia Manhattan y después, con el A* simplificado, -----> NO! Por ahora uso las distancias Manhattan!
    // se calcula la distancia entre esos dos clusters
    private int[][] getClustersDistances(ArrayList<Cluster> clusters, StateObservation stateObs){   
        int num_clusters = clusters.size();
        int[][] dist_matrix = new int[num_clusters][num_clusters]; // Valor inicial -> 0
        int num_gems_1, num_gems_2;
        int min_dist, ind_gem_1 = -1, ind_gem_2 = -1, this_dist;
        PathInformation plan;
        
        // d(a,b) = d(b,a) por lo que solo recorro la diagonal inferior de la matriz
        for (int i = 1; i < num_clusters; i++)
            for (int j = 0; j < i; j++){
                // Calculo la pareja de gemas más cercanas (según dist. Manhattan) para esos dos clusters
                num_gems_1 = clusters.get(i).getNumGems();
                num_gems_2 = clusters.get(j).getNumGems();
                min_dist = 1000;
                
                for (int k = 0; k < num_gems_1; k++)
                    for (int l = 0; l < num_gems_2; l++){
                        this_dist = clusters.get(i).getGem(k).getManhattanDistance(clusters.get(j).getGem(l));
                        
                        if (this_dist < min_dist){
                            min_dist = this_dist;
                            ind_gem_1 = k;
                            ind_gem_2 = l;
                        }
                    }
                 
                // Calculo usando el A* simplificado la distancia entre esas dos gemas
                // SI USO EL A* SIMPLIFICADO Y NO PUEDO LLEGAR A LA GEMA POR UNA ROCA, LA LONGITUD DEL PLAN ES 0!!
                
                /*plan = pathFinder( clusters.get(j).getGem(ind_gem_2).getX(), clusters.get(j).getGem(ind_gem_2).getY(),
                        stateObs,
                        new PlayerObservation(clusters.get(i).getGem(ind_gem_1).getX(),
                                              clusters.get(i).getGem(ind_gem_1).getY(),
                                              Orientation.N) );
                
                dist_matrix[i][j] = dist_matrix[j][i] = plan.plan.size(); // Guardo en la matriz el número de acciones del plan*/
                
                dist_matrix[i][j] = dist_matrix[j][i] = min_dist;
            }
        
        return dist_matrix;
    }

    private StateObservation simulateActions(StateObservation stateObs, LinkedList<Types.ACTIONS> actions) {
        StateObservation newState = stateObs.copy();

        for (Types.ACTIONS action: actions) {
            newState.advance(action);
        }

        return newState;
    }

    private int getHeuristicGems(Observation start, Observation goal, ArrayList<Observation> gems) {
        return getHeuristicGems(start.getX(), start.getY(), goal.getX(), goal.getY(), gems);
    }
    
    // Heurística para el A* que tiene que coger varias gemas
    // Devuelve la heurística asociada a ir desde la casilla xStart, yStart
    // coger todas las gemas de "gems" y acabar en la casilla xGoal, yGoal
    // Cota usada: la distancia es la suma de la distancia de ir de start a
    // la gema más cercana, los "n-1" lados más cortos del grafo formado por las gemas
    // y la distancia de ir desde goal a la gema más cercana
    private int getHeuristicGems(int xStart, int yStart, int xGoal, int yGoal, ArrayList<Observation> gems){
        int total_dist = 0;
        
        // Veo si en mapaCircuitos está guardada la información sobre esta lista de gemas

        int dist_guardada = mapaCircuitos.getOrDefault(gems, -1).intValue();
        
        // La distancia del circuito ya estaba guardada -> solo tengo que calcular
        // la distancia de xStart, yStart a la gema más cercana y sumarle la distancia guardada
        if (dist_guardada != -1){
            int min_dist_orig = 1000;
            int dist_orig;

            for (Observation gem: gems){
                dist_orig = getHeuristicDistance(gem.getX(), gem.getY(), xStart, yStart);

                if (dist_orig < min_dist_orig)
                    min_dist_orig = dist_orig;
            }
            
            total_dist = min_dist_orig + dist_guardada;
        }
        // La distancia no estaba guardada -> la calculo desde 0 y guardo la suma
        // de la distancia de los n-1 lados más cortos y de ir desde goal a la gema
        // más cercana en el hashMap
        else{
            // Primero calculo la distancia optimista (cota inferior) para coger todas las gemas de la lista
            // Calculo una matriz de distancias entre las gemas (usando getHeuristicDistance)
            int num_gems = gems.size();
            int[][] dist_matrix = new int[num_gems][num_gems]; // Matriz triangular inferior

            Observation gem_i;
            int x_gem_i, y_gem_i;
            for (int i = 1; i < num_gems; i++){
                gem_i = gems.get(i);
                x_gem_i = gem_i.getX();
                y_gem_i = gem_i.getY();

                for (int j = 0; j < i; j++){
                    dist_matrix[i][j] = getHeuristicDistance(x_gem_i, y_gem_i, gems.get(j).getX(), gems.get(j).getY());
                }
            }

            // Ahora calculo las "num_gems-1" distancias más pequeñas entre las gemas
            int[] smallest_dist = new int[num_gems-1];

            for (int i = 0; i < num_gems-1; i++)
                smallest_dist[i] = 1000;

            int dist_actual;
            for (int i = 1; i < num_gems; i++){         
                for (int j = 0; j < i; j++){
                    dist_actual = dist_matrix[i][j];

                    // Calculo el máximo del vector smallest_dist
                    int max_dist = -1;
                    int ind_max = -1;
                    for (int k = 0; k < num_gems-1; k++){
                        if (smallest_dist[k] > max_dist){
                            max_dist = smallest_dist[k];
                            ind_max = k;
                        }
                    }

                    // Si dist_actual es menor que ese valor, lo sustituyo
                    if (dist_actual < max_dist)
                        smallest_dist[ind_max] = dist_actual;     
                }
            }
            
            int sum_dist_grafo = 0;
            
            // Le sumo a la distancia total las distancias de smallest_dist
            for (int i = 0; i < num_gems-1; i++){
                total_dist += smallest_dist[i];
                sum_dist_grafo += smallest_dist[i];
            }

            // Calculo las gemas más cercanas al punto de inicio y de fin

            int min_dist_orig = 1000, min_dist_goal = 1000;
            int dist_orig, dist_goal;

            for (Observation gem: gems){
                dist_orig = getHeuristicDistance(gem.getX(), gem.getY(), xStart, yStart);
                dist_goal = getHeuristicDistance(gem.getX(), gem.getY(), xGoal, yGoal);

                if (dist_orig < min_dist_orig)
                    min_dist_orig = dist_orig;

                if (dist_goal < min_dist_goal)
                    min_dist_goal = dist_goal;
            }

            // Sumo esas distancias a la distancia total
            total_dist += min_dist_orig + min_dist_goal;  
            
            // Guardo sum_dist_grafo + min_dist_goal en el mapa, asociado a esta lista de gemas
            mapaCircuitos.put(gems, new Integer(sum_dist_grafo + min_dist_goal));
        }
        
        return total_dist;
    }
}
