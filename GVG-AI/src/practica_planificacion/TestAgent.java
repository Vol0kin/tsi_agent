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
        
        StateObservation estadoActual;
        PlayerObservation posJugador = this.getPlayer(stateObs);
        final PlayerObservation posInicial = this.getPlayer(stateObs);
        ArrayList<Observation>[][] observacionNivel = this.getObservationGrid(stateObs);
        
        final int numFilas = observacionNivel.length,
                  numColumnas = observacionNivel[0].length,
                  numAcciones = 4;
        boolean mapaExplorado[][][] = new boolean[numFilas][numColumnas][numAcciones];
        
        for (int i = 0; i < numFilas; i++) {
            for (int j = 0; j < numColumnas; j++) {
                for (int k = 0; k < numAcciones; k++) {
                    mapaExplorado[i][j][k] = false;
                }
            }
        }
        
        // Distancia recorrida desde la casilla inicial
        int distanciaRecorrida = 0;
        
        // Acciones a aplicar
        Types.ACTIONS[] acciones = {Types.ACTIONS.ACTION_DOWN, Types.ACTIONS.ACTION_RIGHT, 
                                    Types.ACTIONS.ACTION_UP, Types.ACTIONS.ACTION_LEFT};
        
        // Gema a conseguir
        final Observation gema = new Observation(xGema, yGema, ObservationType.GEM);
        boolean gemaEncontrada = false;
        
        listaAbiertos.add(new CasillaCamino(distanciaRecorrida, gema.getManhattanDistance(posJugador),
                                            Types.ACTIONS.ACTION_NIL, stateObs, null));
        
        while (!listaAbiertos.isEmpty() && !gemaEncontrada) {
                       
            nodoActual = listaAbiertos.poll();
            
            observacionNivel = this.getObservationGrid(nodoActual.estado);
            
            posJugador = this.getPlayer(nodoActual.estado);
            distanciaRecorrida = posJugador.getManhattanDistance(posInicial);
            System.out.println(nodoActual.costeF);
            int xJugador = posJugador.getX(),
                yJugador = posJugador.getY();
            
            if (posJugador.getManhattanDistance(gema) == 0) {
                System.out.println("Encontrada");
                gemaEncontrada = true;
            }
            
            if (!gemaEncontrada) {
                int yRocaSup = yJugador - 2 < 0 ? 0 : yJugador - 2;
                System.out.println(posJugador);
                
                boolean rocaIzquierdaSup = observacionNivel[xJugador - 1][yJugador - 1].get(0).getType().equals(ObservationType.BOULDER),
                        rocaDerechaSup = observacionNivel[xJugador - 1][yJugador + 1].get(0).getType().equals(ObservationType.BOULDER),
                        rocaSup = observacionNivel[xJugador][yRocaSup].get(0).getType().equals(ObservationType.BOULDER);
                System.out.println(elapsedTimer.remainingTimeMillis());
                for (int i = 0; i < numAcciones; i++) {
                    Types.ACTIONS accion = acciones[i];
                    
                    boolean saltarAccion = false;
                    estadoActual = nodoActual.estado.copy();
                  
                    if (accion.equals(Types.ACTIONS.ACTION_UP) && rocaSup
                        || accion.equals(Types.ACTIONS.ACTION_LEFT) && rocaIzquierdaSup
                        || accion.equals(Types.ACTIONS.ACTION_RIGHT) && rocaDerechaSup) {
                        System.out.println("saltamos");
                        saltarAccion = true;
                    }
                    
                    if (!saltarAccion) {
                         
                        estadoActual.advance(accion);
                         
                        
                        PlayerObservation nuevaPosJugador = this.getPlayer(estadoActual);
                        
                        boolean mismaPosicion = posJugador.equals(nuevaPosJugador),
                                mismaOrientacion = posJugador.getOrientation().equals(nuevaPosJugador.getOrientation()),
                                addCasilla = false;
                        if (!mismaPosicion) {
                            mapaExplorado[nuevaPosJugador.getX()][nuevaPosJugador.getY()][(i+2)%numAcciones] = true;
                            mapaExplorado[xJugador][yJugador][i] = true;
                            addCasilla = true;
                        } else if (!mismaOrientacion && !mapaExplorado[xJugador][yJugador][i]) {
                            mapaExplorado[xJugador][yJugador][i] = true;
                            addCasilla = true;
                        }
                        //System.out.println(elapsedTimer.remainingTimeMillis());
                        
                        if (addCasilla) {
                            listaAbiertos.add(new CasillaCamino(distanciaRecorrida, 
                                    nuevaPosJugador.getManhattanDistance(gema), accion, 
                                    estadoActual, nodoActual));
                        }                        
                    }                    
                }
                System.out.println(elapsedTimer.remainingTimeMillis());
            }            
            
            listaExplorados.add(nodoActual);
        }
        
        CasillaCamino recorrido = listaExplorados.get(listaExplorados.size() - 1);
        
        // Guardar distancia recorrida  y acciones en la informacion del plan
        if (gemaEncontrada) {
            informacionPlan.distancia = distanciaRecorrida;
        
            while (recorrido.padre != null) {
                System.out.println(recorrido.accion);
                informacionPlan.plan.addFirst(recorrido.accion);
                recorrido = recorrido.padre;
            }
        }
                
    }
}
