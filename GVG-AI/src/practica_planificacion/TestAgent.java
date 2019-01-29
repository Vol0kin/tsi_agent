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
            aStar(7, 9, stateObs);
            
            try{
                Thread.sleep(1000);
            }
            catch(InterruptedException e){}
               
        iter++;
        }
    
        Types.ACTIONS action = Types.ACTIONS.ACTION_NIL;
        
        return action;
    }
    
    private void aStar(int xGema, int yGema, StateObservation stateObs) {
        // Cola de nodos abiertos
        PriorityQueue<CasillaCamino> listaAbiertos = new PriorityQueue<>(
                (CasillaCamino c1, CasillaCamino c2) -> c1.costeF - c2.costeF);
        
        // Lista de nodos explorados
        ArrayList<CasillaCamino> listaExplorados = new ArrayList<>();
        
        // Representa el nodo que se explora a continuacion
        CasillaCamino nodoActual;
        
        StateObservation estadoActual;
        PlayerObservation posJugador = this.getPlayer(stateObs);
        ArrayList<Observation>[][] observacionNivel = this.getObservationGrid(stateObs);
        
        // Distancia recorrida desde la casilla inicial
        int distanciaRecorrida = 0;
        ArrayList<ArrayList<Boolean>> mapaExplorado = new ArrayList<>(observacionNivel[0].length);
        System.out.println("Filas: " + observacionNivel[0].length + " Columnas " + observacionNivel.length);
        
        for (int i = 0; i < observacionNivel[0].length; i++) {
            mapaExplorado.add(new ArrayList<>(observacionNivel.length));
            
            for (int j = 0; j < observacionNivel.length; j++) {
                mapaExplorado.get(i).add(false);
            }
        }
        
        // Acciones a aplicar
        Types.ACTIONS[] acciones = {Types.ACTIONS.ACTION_DOWN, Types.ACTIONS.ACTION_RIGHT, 
                                    Types.ACTIONS.ACTION_UP, Types.ACTIONS.ACTION_LEFT};
        
        // Gema a conseguir
        final Observation gema = new Observation(xGema, yGema, ObservationType.GEM);
        boolean gemaEncontrada = false;
        
        listaAbiertos.add(new CasillaCamino(distanciaRecorrida, gema.getManhattanDistance(posJugador),
                                            Types.ACTIONS.ACTION_NIL, stateObs, null));
        
        while (!listaAbiertos.isEmpty() && !gemaEncontrada) {
            System.out.println("bucle");
            distanciaRecorrida++;
            nodoActual = listaAbiertos.poll();
            
            observacionNivel = this.getObservationGrid(nodoActual.estado);
            
            posJugador = this.getPlayer(nodoActual.estado);
            int xJugador = posJugador.getX(),
                yJugador = posJugador.getY();
            
            mapaExplorado.get(yJugador).set(xJugador, true);
            
            if (posJugador.getManhattanDistance(gema) == 0) {
                gemaEncontrada = true;
            }
            
            if (!gemaEncontrada) {
                System.out.println("condicion");
                int yRocaSup = yJugador - 2 < 0 ? 0 : yJugador - 2;
                System.out.println(observacionNivel[yRocaSup][xJugador].get(0).getType());
                System.out.println("Roca: "+ yRocaSup+", "+xJugador);
                System.out.println(posJugador);
                
                boolean rocaIzquierdaSup = observacionNivel[xJugador - 1][yJugador - 1].get(0).getType().equals(ObservationType.BOULDER),
                        rocaDerechaSup = observacionNivel[xJugador - 1][yJugador + 1].get(0).getType().equals(ObservationType.BOULDER),
                        rocaSup = observacionNivel[yRocaSup][yJugador].get(0).getType().equals(ObservationType.BOULDER);
                
                for (Types.ACTIONS accion : acciones) {
                   // System.out.println("for interno");
                    boolean saltarAccion = false;
                    estadoActual = nodoActual.estado.copy();
                  
                    if (accion.equals(Types.ACTIONS.ACTION_UP) && rocaSup
                        || accion.equals(Types.ACTIONS.ACTION_LEFT) && rocaIzquierdaSup
                        || accion.equals(Types.ACTIONS.ACTION_RIGHT) && rocaDerechaSup) {
                      //  System.out.println("Dentro del if");
                        saltarAccion = true;
                    }
                    
                    if (!saltarAccion) {
                        estadoActual.advance(accion);
                        
                        PlayerObservation nuevaPosJugador = this.getPlayer(estadoActual);
                        
                       // System.out.println("Antigua: " + posJugador );
                       // System.out.println("Nueva: "+nuevaPosJugador);
                        
                        boolean mismaPosicion = posJugador.equals(nuevaPosJugador) && posJugador.getOrientation().equals(nuevaPosJugador.getOrientation());
                        
                        if (!mismaPosicion) {
                            listaAbiertos.add(new CasillaCamino(distanciaRecorrida, 
                                    nuevaPosJugador.getManhattanDistance(gema), accion, 
                                    estadoActual, nodoActual));
                            mapaExplorado.get(nuevaPosJugador.getY()).set(nuevaPosJugador.getX(), true);
                        }                        
                    }                    
                }
            }            
            
            listaExplorados.add(nodoActual);
        }
        System.out.println("Hasta aqui bien3");
        CasillaCamino recorrido = listaExplorados.get(listaExplorados.size() - 1);
        
        // Guardar distancia recorrida  y acciones en la informacion del plan
        informacionPlan.distancia = distanciaRecorrida;
        
        while (recorrido.padre != null) {
            informacionPlan.plan.addFirst(recorrido.accion);
            recorrido = recorrido.padre;
        }        
    }
}
