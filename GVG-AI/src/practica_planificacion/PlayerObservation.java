package practica_planificacion;

/* Clase para representar la información de un jugador en un momento dado.
 Contiene su posición (x, y), según la cuadrícula que ocupa en el mapa, y su
 orientación */

public class PlayerObservation extends Observation{
    private Orientation orientation;
    
    public PlayerObservation(int x, int y, Orientation orientation){
        super(x, y, ObservationType.PLAYER);
        
        this.orientation = orientation;
    }
    
    public Orientation getOrientation(){
        return orientation;
    }
    
    @Override
    public String toString(){
        return (super.toString() + " orientación: " + orientation);
    }
}