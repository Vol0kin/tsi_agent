package practica_busqueda;

// Agente de prueba. Plantilla para un agente de este juego.

import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

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

public class TestAgent extends BaseAgent{
    
    private boolean primerTurno = true;
    PathInformation informacionPlan;
    
    public TestAgent(StateObservation so, ElapsedCpuTimer elapsedTimer){
        super(so, elapsedTimer);
        informacionPlan = new PathInformation();
    }
    
    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
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
        
        
        ArrayList<Observation> gems = new ArrayList();
        int ind = -1;
        LinkedList<Types.ACTIONS> plan = new LinkedList();
        
        if (plan.size() == 0){
            // Veo si tengo el número suficiente de gemas
            
            if (this.getRemainingGems(stateObs) == 0){
                informacionPlan = stateExplorer(this.getExit(stateObs).getX(), this.getExit(stateObs).getY(), stateObs);
                
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
                        informacionPlan = stateExplorer(gems.get(i).getX(), gems.get(i).getY(), stateObs);

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
        
        Types.ACTIONS action = plan.poll();
        
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
        }
        
        return action;
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
        
        listaAbiertos.add(new CasillaCamino(0, objetivo.getManhattanDistance(posInicial),
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
                                                        objetivo.getManhattanDistance(casillaArriba),
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
                                                        objetivo.getManhattanDistance(casillaAbajo),
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
                                                        objetivo.getManhattanDistance(casillaIzquierda),
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
                                                        objetivo.getManhattanDistance(casillaDerecha),
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
            System.out.println("Encontrado objetivo");
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

    private PathInformation stateExplorer(int xGoal, int yGoal, StateObservation stateObs) {
        PathInformation plan = new PathInformation();
        TreeSet<Node> listaAbiertos = new TreeSet<>();
        ArrayList<Node> listaCerrados = new ArrayList<>();
        HashSet<BaseNode> lista = new HashSet<>();          // lista para comprobar si un nodo ha sido explorado

        int accionesUsadas = 0;

        final ObservationType ROCA = ObservationType.BOULDER,
                              MURO = ObservationType.WALL;

        final int NUM_ACCIONES = 5,
                  ARRIBA = 0,
                  DERECHA = 1,
                  ABAJO = 2,
                  IZQUIERDA = 3,
                  PICAR = 4;

        final Types.ACTIONS[] listaAcciones = {Types.ACTIONS.ACTION_UP, Types.ACTIONS.ACTION_RIGHT,
                                               Types.ACTIONS.ACTION_DOWN, Types.ACTIONS.ACTION_LEFT,
                                               Types.ACTIONS.ACTION_USE};

        boolean[] accionesAplicables;
        Node nodoActual;
        boolean encontradoObjetivo = false;
        PlayerObservation posJugador = this.getPlayer(stateObs);
        ArrayList<Observation>[][] observacion = this.getObservationGrid(stateObs);

        listaAbiertos.add(new Node(accionesUsadas, posJugador.getManhattanDistance(observacion[xGoal][yGoal].get(0)),
                            null, -1, stateObs, this.getGemsList(stateObs), posJugador, null ));

        System.out.println("Empieza");
        System.out.println("x: " + xGoal + " y: " + yGoal);

        while (!listaAbiertos.isEmpty() && !encontradoObjetivo) {
            System.out.println(accionesUsadas + " " + listaAbiertos.size());
            accionesUsadas++;
            nodoActual = listaAbiertos.pollFirst();
            System.out.println(nodoActual.getJugador());

            if (xGoal == nodoActual.getJugador().getX() && yGoal == nodoActual.getJugador().getY()) {
                encontradoObjetivo = true;
                System.out.println("Encontrado el objetivo");
            } else {
                StateObservation estadoObservacion = nodoActual.getEstado();
                accionesAplicables = new boolean[] {false, false, false, false, false};

                posJugador = this.getPlayer(estadoObservacion);
                observacion = this.getObservationGrid(estadoObservacion);

                int xActual = posJugador.getX(), yActual = posJugador.getY();

                System.out.println("pos: " + xActual + " " + yActual);
                System.out.println(observacion[xActual][yActual + 1]);

                // Comprobar que acciones pueden ser aplicadas para que casillas

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
                }

                for (int i = 0; i < NUM_ACCIONES; i++) {
                    if (accionesAplicables[i]) {
                        StateObservation forwardState = estadoObservacion.copy();
                        forwardState.advance(listaAcciones[i]);
                        PlayerObservation nuevaPosJugador = this.getPlayer(forwardState);
                        System.out.println(nuevaPosJugador);

                        observacion = this.getObservationGrid(forwardState);

                        // Comprobar si para una posicion y una accion no se ha explorado antes ese nodo
                        if (lista.add(new BaseNode(nuevaPosJugador, listaAcciones[i]))) {
                            listaAbiertos.add(new Node(accionesUsadas,
                                    nuevaPosJugador.getManhattanDistance(observacion[xGoal][yGoal].get(0)),
                                    listaAcciones[i], i, forwardState, this.getGemsList(forwardState),
                                    nuevaPosJugador, nodoActual));
                        }
                    }
                }
            }

            listaCerrados.add(nodoActual);
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
}
