package aeryan;

// Empezar la partida (main) -> tracks.singelPlayer.Test

// Imports de la superclase
import ontology.Types;
import tools.*;
import core.player.*;
import core.game.*;

// Otros imports
import java.util.ArrayList;
import java.util.Random;
import java.awt.Dimension;

// Para el juego que elija, ver cuáles son los diferentes sprites (que devuelven los métodos getXXXPositions)
// y clasificarlos

// El nombre de la clase tiene que ser Agent y heredar de AbstractPlayer
public class Agent extends AbstractPlayer {
    protected Random randomGenerator;

    // <Método obligatorio>
    //Constructor. It must return in 1 second maximum.
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        randomGenerator = new Random();
    }

    // <Método obligatorio>
    //Act function. Called every game step, it must return an action in 40 ms maximum.
    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        /*
        // Random
        //Get the available actions in this game.
        ArrayList<Types.ACTIONS> actions = stateObs.getAvailableActions();

        //Determine an index randomly and get the action to return.
        int index = randomGenerator.nextInt(actions.size());
        Types.ACTIONS action = actions.get(index);

        //Return the action.
        
        
        // Siempre ejecuta la acción USE
        Types.ACTIONS action = Types.ACTIONS.ACTION_USE;
        
        // Probando las observaciones
        Dimension dimensionJuego = stateObs.getWorldDimension();
        int tamBloque = stateObs.getBlockSize(); // Tamaño de un sprite en píxeles
        
        int ancho = dimensionJuego.width / tamBloque; // Cuadrículas de ancho
        int alto = dimensionJuego.height / tamBloque; // Cuadrículas de alto
        
        /*System.out.println("Ancho: " + Integer.toString(ancho) + " Alto: "
                + Integer.toString(alto));
        
        System.out.println("Puntuación: " + stateObs.getGameScore());
        System.out.println("Orientación: " + stateObs.getAvatarOrientation());
        System.out.println("Posición: " + stateObs.getAvatarPosition());
        System.out.println("GameTick: " + stateObs.getGameTick());
        
        // Cuadrícula de objetos
        ArrayList<Observation>[][] cuadricula = stateObs.getObservationGrid();
        
      
        /*for (int x = 0; x < ancho; x++)
            for (int y = 0; y < alto; y++){
                if (!cuadricula[x][y].isEmpty()){              
                    Observation objeto = cuadricula[x][y].get(0); // Objeto en cuadrado (i, j)
                    System.out.println(objeto.toString());
                }
            }*/
        
        // Imprimo la posición del avatar
        
        Vector2d posicion_pixeles = stateObs.getAvatarPosition(); // Posición en píxeles
        
        Vector2d posicion_casilla = new Vector2d(posicion_pixeles.x / stateObs.getBlockSize(), 
            posicion_pixeles.y / stateObs.getBlockSize()); // Posición en casillas (cuadrículas)
        
        System.out.println("Posición: " + posicion_casilla);
        
        ArrayList<Observation>[][] cuadricula = stateObs.getObservationGrid();
        
        Dimension dimensionJuego = stateObs.getWorldDimension();
        int tamBloque = stateObs.getBlockSize(); // Tamaño de un sprite en píxeles
        
        int ancho = dimensionJuego.width / tamBloque; // Cuadrículas de ancho
        int alto = dimensionJuego.height / tamBloque; // Cuadrículas de alto
        
        for (int y = 0; y < alto; y++)
            for (int x = 0; x < ancho; x++){
                if (!cuadricula[x][y].isEmpty()){              
                    Observation objeto = cuadricula[x][y].get(0); // Objeto en cuadrado (i, j)
                    System.out.println("Fila: " + Integer.toString(y) + " Columna: " + Integer.toString(x) + ' ' 
                            + objeto.toString());
                }
            }
        
        // ids de las observaciones (itype = ... (no category!))
        
        // Cuidado si dos observaciones están en el mismo sitio!
        // Mismo itype de las observaciones de los juegos 10 y 11!!
        // Todos los niveles tienen los mismos elementos (observaciones)
        
        // 0 -> muro (tanto los bordes como los muros internos al mapa)
        // 4 -> suelo (no excavado)
        // 7 -> roca
        // 6 -> gema
        // 11 -> murciélago (rojo)
        // CUANDO HAY UNA CASILLA EXCAVADA (SIN SUELO) NO HAY NINGUNA OBSERVACIÓN EN ESA CUADRÍCULA!!
        // 10 -> escorpión (blanco)
        // 1 -> jugador
        // 5 -> salida
        
        return Types.ACTIONS.ACTION_NIL;
    }
}
