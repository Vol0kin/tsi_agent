package practica_planificacion;

// Agente de prueba. Plantilla para un agente de este juego.

import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;

import java.util.LinkedList;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.PriorityQueue;

import java.util.Collections;

public class TestAgent extends BaseAgent{
    
    private int iter = 0;
    PathInformation informacionPlan;
    
    public TestAgent(StateObservation so, ElapsedCpuTimer elapsedTimer){
        super(so, elapsedTimer);
        informacionPlan = new PathInformation();
    }
    
    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
        //System.out.println(elapsedTimer.remainingTimeMillis());
        if (iter == 0){
            
            // Observation Grid
            /*
            ArrayList<Observation>[][] grid = this.getObservationGrid(stateObs);
        
            for (int y = 0; y < grid[0].length; y++)
                for (int x = 0; x < grid.length; x++)
                    System.out.println("Fil: " + y + " Col: " + x + " Obj: " + 
                            grid[x][y].get(0).getType());
            */
            
            // Obtener lista de enemigos
            /*
            ArrayList<Observation>[] enemies = this.getEnemiesList(stateObs);
        
            for (int i = 0; i < enemies.length; i++)
                for (Observation obs : enemies[i])
                    System.out.println(obs);*/
            
            /*
            ArrayList<Observation> bats = this.getBatsList(stateObs);
            ArrayList<Observation> scorpions = this.getScorpionsList(stateObs);
            
            System.out.println("Bats:");
            for (Observation bat : bats)
                System.out.println(bat);
            
            System.out.println("Scorpions:");
            for (Observation scorpion : scorpions)
                System.out.println(scorpion);*/
                       
            /* // Gemas
            ArrayList<Observation> gems = this.getGemsList(stateObs);
        
            for (Observation obs : gems)
                System.out.println(obs);*/
            
            // Wall, Ground, Boulder, Empty
            /*
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
            */
            
            // Salida
            /*
            Observation exit = this.getExit(stateObs);           
            System.out.println(exit);*/
            
            // Jugador
            /*
            PlayerObservation player = this.getPlayer(stateObs);
            System.out.println(player);*/      
            
            // Número de gemas
                    
            // System.out.println(this.getNumGems(stateObs));
            
            // Distancias y colisiones
            /*
            PlayerObservation player = this.getPlayer(stateObs);
            Observation enemy = this.getScorpionsList(stateObs).get(0);
            
            System.out.println("Colisionan?: " + player.collides(enemy));
            System.out.println("Distancia Euclídea: " + player.getEuclideanDistance(enemy));
            System.out.println("Distancia Manhattan: " + player.getManhattanDistance(enemy));
            */
            aStar(7, 9, stateObs, elapsedTimer);
            System.out.println(elapsedTimer.remainingTimeMillis());
            
            try{
                Thread.sleep(1000);
            }
            catch(InterruptedException e){}
               
        iter++;
        }
    
        Types.ACTIONS action = informacionPlan.plan.poll();
        
        return action;
    }
    
    private void aStar(int xGema, int yGema, StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        // Cola de nodos abiertos
        PriorityQueue<CasillaCamino> listaAbiertos = new PriorityQueue<>(
                (CasillaCamino c1, CasillaCamino c2) -> c1.costeF - c2.costeF);
        
        // Lista de nodos explorados
        ArrayList<CasillaCamino> listaExplorados = new ArrayList<>();
        
        // Representa el nodo que se explora a continuacion
        CasillaCamino nodoActual;
        
        final ObservationType muro = ObservationType.WALL,
                              roca = ObservationType.BOULDER;
        
        
        PlayerObservation posJugador = this.getPlayer(stateObs);
        final PlayerObservation posInicial = this.getPlayer(stateObs);
        ArrayList<Observation>[][] observacionNivel = this.getObservationGrid(stateObs);
        Observation observacionActual;
        
        final int numFilas = observacionNivel.length,
                  numColumnas = observacionNivel[0].length;
        
        boolean mapaExplorado[][] = new boolean[numFilas][numColumnas];
        
        for (int i = 0; i < numFilas; i++) {
            for (int j = 0; j < numColumnas; j++) {
                mapaExplorado[i][j] = false;
            }
        }
        
        // Distancia recorrida desde la casilla inicial
        int distanciaRecorrida = 0;       
        
        // Gema a conseguir
        final Observation gema = observacionNivel[xGema][yGema].get(0);
        boolean gemaEncontrada = false;
        
        listaAbiertos.add(new CasillaCamino(distanciaRecorrida, gema.getManhattanDistance(posJugador),
                                            posJugador.getOrientation(), null, posJugador, null));
        
        mapaExplorado[posJugador.getX()][posJugador.getY()] = true;
        
        while (!listaAbiertos.isEmpty() && !gemaEncontrada) {
                       
            nodoActual = listaAbiertos.poll();
            
            observacionActual = nodoActual.observacion;
            
            int xActual = observacionActual.getX(),
                yActual = observacionActual.getY();
            
            if (gema.equals(observacionActual)) {
                gemaEncontrada = true;
            } else {
                
                LinkedList<Types.ACTIONS> acciones;
                
                int yArriba = yActual - 1,
                    yAbajo = yActual + 1,
                    xIzquierda = xActual - 1,
                    xDerecha = xActual + 1;
            
                Observation casillaArriba = observacionNivel[xActual][yArriba].get(0),
                            casillaAbajo = observacionNivel[xActual][yAbajo].get(0),
                            casillaDerecha = observacionNivel[xDerecha][yActual].get(0),
                            casillaIzquierda = observacionNivel[xIzquierda][yActual].get(0);
                
                int yArribaRoca = yArriba - 1 < 0 ? 0 : yArriba - 1;
                
                // Descendiente superior
                if (!mapaExplorado[xActual][yArriba] &&
                    !casillaArriba.getType().equals(muro) && !casillaArriba.getType().equals(roca) &&
                    !observacionNivel[xActual][yArribaRoca].get(0).getType().equals(roca)) {
                    
                
                    acciones = new LinkedList<>();
                    acciones.addFirst(Types.ACTIONS.ACTION_UP);
                    
                    if (!nodoActual.orientacion.equals(Orientation.N)) {
                        acciones.addFirst(Types.ACTIONS.ACTION_UP);
                    }
                    
                    listaAbiertos.add(new CasillaCamino(posInicial.getManhattanDistance(observacionActual),
                                                        gema.getManhattanDistance(observacionActual),
                                                        Orientation.N, acciones, casillaArriba, nodoActual));
                }
                
                mapaExplorado[xActual][yArriba] = true;
                
                // Descendiente inferior
                if (!mapaExplorado[xActual][yAbajo] &&
                    !casillaAbajo.getType().equals(muro) && !casillaAbajo.getType().equals(roca)) {
                
                    acciones = new LinkedList<>();
                    acciones.addFirst(Types.ACTIONS.ACTION_DOWN);
                    
                    if (!nodoActual.orientacion.equals(Orientation.S)) {
                        acciones.addFirst(Types.ACTIONS.ACTION_DOWN);
                    }
                    
                    listaAbiertos.add(new CasillaCamino(posInicial.getManhattanDistance(observacionActual),
                                                        gema.getManhattanDistance(observacionActual),
                                                        Orientation.S, acciones, casillaAbajo, nodoActual));
                }
                
                mapaExplorado[xActual][yAbajo] = true;
                
                // Descendiente izquierdo
                if (!mapaExplorado[xIzquierda][yActual] &&
                    !casillaIzquierda.getType().equals(muro) && !casillaIzquierda.getType().equals(roca) &&
                    !observacionNivel[xIzquierda][yArriba].get(0).getType().equals(roca)) {
                    
                
                    acciones = new LinkedList<>();
                    acciones.addFirst(Types.ACTIONS.ACTION_LEFT);
                    
                    if (!nodoActual.orientacion.equals(Orientation.W)) {
                        acciones.addFirst(Types.ACTIONS.ACTION_LEFT);
                    }
                    
                    listaAbiertos.add(new CasillaCamino(posInicial.getManhattanDistance(observacionActual),
                                                        gema.getManhattanDistance(observacionActual),
                                                        Orientation.W, acciones, casillaIzquierda, nodoActual));
                }
                
                mapaExplorado[xIzquierda][yActual] = true;
                
                // Descendiente derecho
                if (!mapaExplorado[xDerecha][yActual] &&
                    !casillaDerecha.getType().equals(muro) && !casillaDerecha.getType().equals(roca) &&
                    !observacionNivel[xDerecha][yArriba].get(0).getType().equals(roca)) {
                    
                
                    acciones = new LinkedList<>();
                    acciones.addFirst(Types.ACTIONS.ACTION_RIGHT);
                    
                    if (!nodoActual.orientacion.equals(Orientation.E)) {
                        acciones.addFirst(Types.ACTIONS.ACTION_RIGHT);
                    }
                    
                    listaAbiertos.add(new CasillaCamino(posInicial.getManhattanDistance(observacionActual),
                                                        gema.getManhattanDistance(observacionActual),
                                                        Orientation.E, acciones, casillaDerecha, nodoActual));
                }
                
                mapaExplorado[xDerecha][yActual] = true;
                
            }                  
            
            listaExplorados.add(nodoActual);
        }
        
        CasillaCamino recorrido = listaExplorados.get(listaExplorados.size() - 1);
        
        // Guardar distancia recorrida  y acciones en la informacion del plan
        if (gemaEncontrada) {
            informacionPlan.distancia = posInicial.getManhattanDistance(gema);
        
            while (recorrido.padre != null) {
                // Aniadir casillas recorridas
                informacionPlan.listaCasillas.add(0, recorrido.observacion);
                
                // Aniadir secuencia de acciones realizadas
                informacionPlan.plan.addAll(0, recorrido.acciones);
                recorrido = recorrido.padre;
            }
            
            // Aniadir casilla inicial
            informacionPlan.listaCasillas.add(0, recorrido.observacion);
        }                
    }
}
