package practica_planificacion;

// Agente de prueba. Plantilla para un agente de este juego.

import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;

import java.util.LinkedList;

public class TestAgent extends BaseAgent{
    
    private int iter = 0;
    LinkedList<Types.ACTIONS> plan;
    
    public TestAgent(StateObservation so, ElapsedCpuTimer elapsedTimer){
        super(so, elapsedTimer);
        plan = new LinkedList();
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
            
            try{
                Thread.sleep(1000);
            }
            catch(InterruptedException e){}
               
        iter++;
        }
    
        Types.ACTIONS action = Types.ACTIONS.ACTION_NIL;
        
        return action;
    }
}
