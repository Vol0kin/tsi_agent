package practica_busqueda;

import java.util.ArrayList;

// Clúster de gemas
// Guarda toda la información necesaria sobre el clúster

public class Cluster {
    private ArrayList<Observation> gems; // Gemas del clúster
    private int pathLength; // Longitud aproximada del camino que recorre todas las gemas del clúster
     
    public Cluster(){
        this.pathLength = 0;
        this.gems = new ArrayList();
    }
       
    public Observation getGem(int i){
        return gems.get(i);
    }
    
    public int getNumGems(){
        return gems.size();
    }
    
    public int getPathLenght(){
        return pathLength;
    }
    
    public void addGem(Observation newGem){
        gems.add(newGem);
    }
    
    // Calcula la longitud del camino más corto que desde una gema pasa por todas las demás
    // Algoritmo: para cada gema calculo la distancia Manhattan con la gema más cercana
    // y sumo estas distancias al pathLenght excepto para la última de las gemas
    
    public void calculatePathLength(){
        if (gems.size() == 1)
            pathLength = 0;
        else{      
            int numGems = gems.size();
            int minDist;
            int dist;
            Observation thisGem;

            // Recorro todas las gemas menos la última
            for (int i = 0; i < numGems - 1; i++){
                minDist = 1000;
                thisGem = gems.get(i);

                for (int j = 0; j < numGems; j++){
                    dist = thisGem.getManhattanDistance(gems.get(j));
                    
                    if (dist != 0 && dist < minDist)
                        minDist = dist;
                } 
                pathLength += minDist;
            }
        }
    }
}
