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
    private SearchInformation searchInfo;
    
    int it = 0;
    
    private PathFinder pf;
    private boolean stop;
    
    private HashMap<ArrayList<Observation>, Integer> mapaCircuitos; // Al iniciar
    
    private ClusterInformation clusterInf;
    
    // Información extra para la planificación
    
    private int sig_cluster = 0; // Indice del siguiente clúster a coger del objetivo
    
    private boolean en_cluster = false; // Vale false si estamos yendo hacia el siguiente clúster y true si ya hemos llegado y estamos cogiendo sus gemas
    
    // Variables para guardar los parámetros del A* cuando se le llama a lo largo de varios turnos
    private int x_search;
    private int y_search;
    private ArrayList<Observation> gems_search;
    
    private LinkedList<Types.ACTIONS> plan_no_morir; // Plan para guardar las acciones a ejecutar para evitar morir -> si no está vacío siempre se ejecutan sus acciones
    private boolean hay_que_replanificar = false; // Vale true cuando se ha ejecutando/está ejecutando el plan_no_morir -> cuando se haya terminado de ejecutar ese plan, se replanifica con el clúster actual (si tiene gemas)
    
    private boolean abandonando_nivel = false; // Vale true cuando se esté ejecutando el plan para salir del nivel, tras conseguir 9 gemas o más
    
    private int it_ultimo_movimiento = 0; // Dice en qué iteración se hizo el último movimiento (desplazamiento de una casilla a otra)
    
    private int last_x, last_y; // Posición x e y del jugador en el turno anterior
    
    // EN EL CONSTRUCTOR TENGO MÁS TIEMPO PARA PLANIFICAR!!!!!
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
        searchInfo = new SearchInformation();
        
        plan_no_morir = new LinkedList<>();
        
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
        
        clusterInf = new ClusterInformation(); // Creo la información de los clústeres
        
        PlayerObservation jugador = this.getPlayer(so);
        last_x = jugador.getX();
        last_y = jugador.getY();
        
        // Información extra para la planificación
        
        
        //informacionPlan = this.pathExplorer(14, 5, so);
    }
    
    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        //System.out.println(elapsedTimer.remainingTimeMillis());
        
        /*ArrayList<Observation> gems = new ArrayList();
        int ind = -1;
        LinkedList<Types.ACTIONS> plan = new LinkedList();*/
        /*
        if (it == 0) {
            double t1 = System.currentTimeMillis();
            //informacionPlan = pathExplorer(9, 4, stateObs); // Tarda 1 ms aprox.
            //informacionPlan = pathExplorer(1, 4, stateObs);
            // informacionPlan = stateExplorer(9, 4, stateObs); -> tarda unos 63 ms!!
            
            informacionPlan = pathExplorer(24, 1, stateObs); // NO LLEGA PORQUE SE CHOCA CON LAS ROCA QUE ESTAN CAYENDO!!!
            
            double t2 = System.currentTimeMillis();
            System.out.println("Tiempo: " + (t2 - t1) + " ms");
        }

        if (it > 0 && informacionPlan.plan.isEmpty()) {
            PlayerObservation pos = this.getPlayer(stateObs);
            long threshold = 5;

            ArrayList<Observation> goalGems = new ArrayList<>();
            ArrayList<Observation>[][] grid = this.getObservationGrid(stateObs);
            goalGems.add(grid[5][3].get(0));
            goalGems.add(grid[6][3].get(0));
            goalGems.add(grid[7][3].get(0));
            goalGems.add(grid[6][1].get(0));
            goalGems.add(grid[7][1].get(0));
            goalGems.add(grid[1][4].get(0));
            informacionPlan = pathExplorer(pos, 10, 1, stateObs, goalGems, elapsedTimer, threshold);

            for (Types.ACTIONS action: informacionPlan.plan) {
                System.out.println(action);
            }

            for (Observation obs: informacionPlan.listaCasillas) {
                System.out.println(obs);
            }
        }

        plan = informacionPlan.plan;
        
        it++;

        if (informacionPlan.plan.isEmpty())
            return Types.ACTIONS.ACTION_NIL;
        
        return plan.pollFirst();*/
        
        
        
        /*
        PlayerObservation jugador = this.getPlayer(stateObs);
        
        if (it == 0){ // Primera iteración -> creo los clústeres y el circuito <Tarda 4 ms>
            clusterInf.createClusters(3, this.getGemsList(stateObs),
                    this.getBouldersList(stateObs), this.getWallsList(stateObs)); // Creo los clusters
            
            // ¡Elimino el clúster 5 al que no se puede llegar!
            clusterInf.clusters.remove(5);
            
            this.saveClustersDistances(clusterInf); // Guardo la matriz de distancias
            this.saveCircuit(clusterInf, this.getPlayer(stateObs).getX(),
            this.getPlayer(stateObs).getY(), this.getExit(stateObs).getX(),
            this.getExit(stateObs).getY()); // Creo el camino a través de los clústeres
            
            for (Observation gem : clusterInf.clusters.get(1).getGems())
                System.out.println(gem);
            
            // Voy a por el primer clúster -> NO PUEDE HABER UNA ROCA EN LA CASILLA FINAL
            
            informacionPlan = pathExplorer(jugador, jugador.getX(), jugador.getY()+1,
                                         stateObs, clusterInf.clusters.get(1).getGems(),
                                         elapsedTimer, 15);
        }
        else if (!informacionPlan.foundPath){ // Todavía no ha terminado la búsqueda
            informacionPlan = pathExplorer(jugador, jugador.getX(), jugador.getY()+1,
                                         stateObs, clusterInf.clusters.get(1).getGems(),
                                         elapsedTimer, 15);
            System.out.println("It " + it + " - búsqueda no terminada");
        }
        
        it++;


        if (informacionPlan.foundPath){
            System.out.println("It " + it + " - búsqueda terminada!!");

            if (informacionPlan.plan.isEmpty()) {
                return Types.ACTIONS.ACTION_NIL;
            }

            return informacionPlan.plan.pollFirst();
        }
        else
            return Types.ACTIONS.ACTION_NIL;*/
        
        
        // DESDE AQUI ----------------------------------
        
        PlayerObservation jugador = this.getPlayer(stateObs);
        Observation salida = this.getExit(stateObs);
        ArrayList<Integer> circuito_prov = new ArrayList<>(); // Uso este circuito en vez del real de forma provisional
        Types.ACTIONS accion = Types.ACTIONS.ACTION_NIL; // Acción que se va a ejecutar este turno
        circuito_prov.add(1);
        circuito_prov.add(0);
        //circuito_prov.add(4);
        
        // Primera iteración
        if (it == 0){ // planifico para acercarme al primer clúster
            clusterInf.createClusters(3, this.getGemsList(stateObs),
                    this.getBouldersList(stateObs), this.getWallsList(stateObs)); // Creo los clusters
            
            // > ¡Elimino el clúster 5 al que no se puede llegar!
            //clusterInf.clusters.remove(5); // VER LO QUE PASA CUANDO NO ENCUENTRA CAMINO
            
            this.saveClustersDistances(clusterInf); // Guardo la matriz de distancias
            this.saveCircuit(clusterInf, jugador.getX(),
            jugador.getY(), salida.getX(),
            salida.getY()); // Creo el camino a través de los clústeres
            
            gems_search = clusterInf.clusters.get(circuito_prov.get(sig_cluster)).getGems();
            
            int[] casilla_search = this.getPuntoIntermedioClusters(gems_search,
                    clusterInf.clusters.get(circuito_prov.get(sig_cluster+1)).getGems(), stateObs);
            
            x_search = casilla_search[0];
            y_search = casilla_search[1]; // --> Se puede escoger como punto final una gema!!
            
            System.out.println(x_search + " " + y_search);
            
            informacionPlan = pathExplorer(x_search, y_search,
                                         stateObs, gems_search,
                                         elapsedTimer, 2);
        }
        
        // Compruebo si al aplicar la última acción nos hemos movido de casilla
        
        if (jugador.getX() != last_x || jugador.getY() != last_y)
            it_ultimo_movimiento = it - 1;
        
        last_x = jugador.getX();
        last_y = jugador.getY();
        
        
        if (!plan_no_morir.isEmpty()){ // Si tengo acciones del plan para no morir, las ejecuto y termino el act
            it++;
            System.out.println("Ejecutando acción para no morir: " + plan_no_morir.peekFirst());
            return plan_no_morir.pollFirst();
        }
        
        
        // Si tengo 9 gemas, planifico para abandonar el nivel
        if (this.getNumGems(stateObs) >= NUM_GEMS_FOR_EXIT && !abandonando_nivel){
            System.out.println("Voy a abandonar el nivel - x: " + this.getPlayer(stateObs).getX() + " y: " + this.getPlayer(stateObs).getY() + " or: " + this.getPlayer(stateObs).getOrientation());
            abandonando_nivel = true;
            
            Observation level_exit = this.getExit(stateObs);
            x_search = level_exit.getX();
            y_search = level_exit.getY();
            
            informacionPlan = pathExplorer(x_search, y_search, stateObs);
            System.out.println("Plan: " + informacionPlan.plan);
            
            informacionPlan.searchComplete = true; // Tiene que terminar en un turno
        }
        
        
        if (!hay_que_replanificar && it != 0 && !informacionPlan.searchComplete) { // Si no ha encontrado camino, sigo buscando
            informacionPlan = pathExplorer(x_search, y_search,
                    stateObs, gems_search,
                    elapsedTimer, 2);
        }

        // Tengo que replanificar
        // Veo si quedan gemas en el clúster actual y si es así cojo las que quedan
        // Si no quedan gemas, me voy al punto intermedio entre este clúster y el siguiente
        // Si tengo 9 gemas me voy a la salida
        if (hay_que_replanificar){
            hay_que_replanificar = false;
            
            if (abandonando_nivel){ // Veo si tengo que planificar para abandonar el nivel
                Observation salida_nivel = this.getExit(stateObs);
                
                informacionPlan = pathExplorer(salida_nivel.getX(), salida_nivel.getY(), stateObs);
                informacionPlan.searchComplete = true; // Tiene que terminar en un turno
            }
            
            else{ // Planifico para coger las gemas del clúster o ir al punto intermedio con el siguiente
                // Veo si quedan gemas en el clúster
                Cluster this_cluster = clusterInf.clusters.get(circuito_prov.get(sig_cluster));
                this_cluster.removeCapturedGems(this.getGemsList(stateObs));

                if (this_cluster.getNumGems() > 0){ // Todavía le quedan gemas
                    gems_search = this_cluster.getGems();

                    informacionPlan = pathExplorer(x_search, y_search,
                                             stateObs, gems_search,
                                             elapsedTimer, 2);

                    if (informacionPlan.searchComplete){ // Veo si ha terminado la planificación en el mismo turno
                        accion = informacionPlan.plan.peekFirst(); // No la borro por si no se ejecuta después
                    }
                    else{             
                        accion = Types.ACTIONS.ACTION_NIL;
                    }
                }
                else{ // No le quedan gemas -> uso el A* simple para irme al punto entre este clúster y el siguiente
                    x_search = 14; // CAMBIARLO CUANDO META EL CIRCUITO!
                    y_search = 5;

                    informacionPlan = pathExplorer(x_search, y_search, stateObs);

                    informacionPlan.searchComplete = true; // Tiene que terminar en un turno
                }
            }
        }
        
        
        if (informacionPlan.searchComplete){ // Si ya ha encontrado camino, se ejecuta
            //System.out.println("Camino encontrado - it=" + it);
            //System.out.println(informacionPlan.plan);

            if (informacionPlan.plan.isEmpty()) { // Si he acabado de ejecutar el plan, planifico para el clúster siguiente
                System.out.println("Plan vacío");
                sig_cluster++;
                gems_search = clusterInf.clusters.get(circuito_prov.get(sig_cluster)).getGems();
                x_search = 14;
                y_search = 5;
                
                //System.out.println(gems_search);
                
                informacionPlan = pathExplorer(x_search, y_search,
                                         stateObs, gems_search,
                                         elapsedTimer, 2);
                
                if (informacionPlan.searchComplete){ // Veo si ha terminado la planificación en el mismo turno
                    accion = informacionPlan.plan.peekFirst(); // No la borro por si no se ejecuta después
                }
                else{             
                    accion = Types.ACTIONS.ACTION_NIL;
                }

            } else{
                accion = informacionPlan.plan.peekFirst();
            }
        }
        
        // <<Parte reactiva>>
        // Ya he elegido la acción. Ahora veo si se puede ejecutar
     
        StateObservation estado_avanzado = stateObs.copy();
        estado_avanzado.advance(informacionPlan.plan.peekFirst()); // Siguiente estado del juego (en el siguiente turno)  
        PlayerObservation jugador_sig_estado = this.getPlayer(estado_avanzado); // Jugador el siguiente turno
        
        if (informacionPlan.plan.size() >= 2)
            estado_avanzado.advance(informacionPlan.plan.get(1)); // Estado del juego dentro de dos turnos
        
        PlayerObservation jugador_sig2_estado = this.getPlayer(estado_avanzado); // Jugador dentro de 2 turnos o dentro de 1 si el plan solo tiene una acción
        
        
        // <Ver si muere>
        // Veo si el jugador va a morir en los siguientes 2 turnos
        
        // BUG: Si el jugador abandona el nivel es como si hubiera muerto!!! (su "x" también vale -1)
        boolean bug_morir = false;
        
        if (jugador.getManhattanDistance(this.getExit(stateObs)) <= 1)
            bug_morir = true;
        
        else if (!jugador_sig_estado.hasDied() && jugador_sig2_estado.hasDied()){
            if (jugador_sig_estado.getManhattanDistance(this.getExit(stateObs)) <= 1)
                bug_morir = true;
        }
        
        if (!bug_morir && (jugador_sig_estado.hasDied() || jugador_sig2_estado.hasDied()) ){ // TENER EN CUENTA QUE LOS ENEMIGOS SON ESTOCÁSTICOS!!
            System.out.println("Va a morir! - it: " + it);
            System.out.println(accion);
            
            // Si va a morir ejecuto las acciones necesarias para sobrevivir y después vuelvo a la casilla donde estaba y sigo ejecutando el plan
            
            // Veo si va a morir debido a una roca y no por un enemigo
            int jug_x = jugador.getX();
            int jug_y = jugador.getY();
            Orientation jug_orient = jugador.getOrientation();
            ArrayList<Observation> [][] grid = this.getObservationGrid(stateObs);
            boolean muerte_por_roca = false;
            boolean roca_derecha = false;
            boolean roca_arriba = false;
            boolean roca_izquierda = false;
            
            if (    grid[jug_x+1][jug_y].get(0).getType() == ObservationType.BOULDER ||
                    grid[jug_x+1][jug_y-1].get(0).getType() == ObservationType.BOULDER){ // Hay roca a la derecha
                    
                    roca_derecha = true;
                    
                    if (informacionPlan.plan.peekFirst() == Types.ACTIONS.ACTION_RIGHT)
                        muerte_por_roca = true;
                
            }
            if (    grid[jug_x-1][jug_y].get(0).getType() == ObservationType.BOULDER ||
                    grid[jug_x-1][jug_y-1].get(0).getType() == ObservationType.BOULDER){ // Hay roca a la izquierda
                    
                    roca_izquierda = true;
                    
                    if (informacionPlan.plan.peekFirst() == Types.ACTIONS.ACTION_LEFT)
                        muerte_por_roca = true;
                
            }
            if (    grid[jug_x][jug_y].get(0).getType() == ObservationType.BOULDER ||
                    grid[jug_x][jug_y-1].get(0).getType() == ObservationType.BOULDER){ // Hay roca arriba
                    
                    roca_arriba = true;
                    
                    if (informacionPlan.plan.peekFirst() == Types.ACTIONS.ACTION_UP)
                        muerte_por_roca = true;
                
            }
            
            
            if (muerte_por_roca){ // Me pongo a salvo en una posición donde no vaya a morir por una roca
                StateObservation estado_prueba;
                plan_no_morir = new LinkedList<>();
                boolean va_a_morir;
                
                // Pruebo primero a quedarme quieto si no me está cayendo una roca encima
                if (!roca_arriba){
                    va_a_morir = false;
                    
                    // Compruebo si al ejecutar ACTION_NIL los dos siguientes turnos el jugador muere o no
                    estado_prueba = stateObs.copy();
                    estado_prueba.advance(Types.ACTIONS.ACTION_NIL);
                    
                    if (this.getPlayer(estado_prueba).hasDied())
                        va_a_morir = true;
                    else{
                        estado_prueba.advance(Types.ACTIONS.ACTION_NIL);
                        
                        if (this.getPlayer(estado_prueba).hasDied())
                            va_a_morir = true;
                    }
                    
                    if (!va_a_morir){ // Si no muere, añado esas acciones al plan
                        plan_no_morir.add(Types.ACTIONS.ACTION_NIL);
                        plan_no_morir.add(Types.ACTIONS.ACTION_NIL);
                        System.out.println("Me quedo quieto");
                    }
                }
                
                // Si no puedo quedarme quieto, pruebo a moverme a la izquierda
                if (plan_no_morir.isEmpty() && !roca_izquierda){
                    va_a_morir = false;
                    
                    if (jug_orient == Orientation.W){ // No tengo que girar
                        // Ejecuto ACTION_LEFT y ACTION_NIL y veo si muero
                        estado_prueba = stateObs.copy();
                        estado_prueba.advance(Types.ACTIONS.ACTION_LEFT);

                        if (this.getPlayer(estado_prueba).hasDied())
                            va_a_morir = true;
                        else{
                            estado_prueba.advance(Types.ACTIONS.ACTION_NIL);

                            if (this.getPlayer(estado_prueba).hasDied())
                                va_a_morir = true;
                        }
                        
                        if (!va_a_morir){ // Si no muere, añado esas acciones al plan
                            plan_no_morir.add(Types.ACTIONS.ACTION_LEFT);
                            plan_no_morir.add(Types.ACTIONS.ACTION_NIL);
                            System.out.println("Me muevo hacia la izquierda");
                        }
                    }
                    else{ // Primero giro a la izquierda y después me muevo
                        // Ejecuto ACTION_LEFT y ACTION_LEFT y veo si muero
                        estado_prueba = stateObs.copy();
                        estado_prueba.advance(Types.ACTIONS.ACTION_LEFT);

                        if (this.getPlayer(estado_prueba).hasDied())
                            va_a_morir = true;
                        else{
                            estado_prueba.advance(Types.ACTIONS.ACTION_LEFT);

                            if (this.getPlayer(estado_prueba).hasDied())
                                va_a_morir = true;
                        }
                        
                        if (!va_a_morir){ // Si no muere, añado esas acciones al plan
                            plan_no_morir.add(Types.ACTIONS.ACTION_LEFT);
                            plan_no_morir.add(Types.ACTIONS.ACTION_LEFT);
                            System.out.println("Me muevo hacia la izquierda");
                        }
                    }   
                }
                
                // Si no ha funcionado, pruebo a moverme a la derecha
                if (plan_no_morir.isEmpty() && !roca_derecha){
                    va_a_morir = false;
                    
                    if (jug_orient == Orientation.E){ // No tengo que girar
                        // Ejecuto ACTION_RIGHT y ACTION_NIL y veo si muero
                        estado_prueba = stateObs.copy();
                        estado_prueba.advance(Types.ACTIONS.ACTION_RIGHT);

                        if (this.getPlayer(estado_prueba).hasDied())
                            va_a_morir = true;
                        else{
                            estado_prueba.advance(Types.ACTIONS.ACTION_NIL);

                            if (this.getPlayer(estado_prueba).hasDied())
                                va_a_morir = true;
                        }
                        
                        if (!va_a_morir){ // Si no muere, añado esas acciones al plan
                            plan_no_morir.add(Types.ACTIONS.ACTION_RIGHT);
                            plan_no_morir.add(Types.ACTIONS.ACTION_NIL);
                            System.out.println("Me muevo hacia la derecha");
                        }
                    }
                    else{ // Primero giro a la izquierda y después me muevo
                        // Ejecuto ACTION_RIGHT y ACTION_RIGHT y veo si muero
                        estado_prueba = stateObs.copy();
                        estado_prueba.advance(Types.ACTIONS.ACTION_RIGHT);

                        if (this.getPlayer(estado_prueba).hasDied())
                            va_a_morir = true;
                        else{
                            estado_prueba.advance(Types.ACTIONS.ACTION_RIGHT);

                            if (this.getPlayer(estado_prueba).hasDied())
                                va_a_morir = true;
                        }
                        
                        if (!va_a_morir){ // Si no muere, añado esas acciones al plan
                            plan_no_morir.add(Types.ACTIONS.ACTION_RIGHT);
                            plan_no_morir.add(Types.ACTIONS.ACTION_RIGHT);
                            System.out.println("Me muevo hacia la derecha");
                        }
                    }   
                }
                
                // Por último, si no ha funcionado nada, me muevo hacia abajo
                if (plan_no_morir.isEmpty()){
                    System.out.println("Me muevo hacia abajo");
                    
                    estado_prueba = stateObs.copy();
                    estado_prueba.advance(Types.ACTIONS.ACTION_DOWN);
                    estado_prueba.advance(Types.ACTIONS.ACTION_DOWN);
                    plan_no_morir.add(Types.ACTIONS.ACTION_DOWN);
                    
                    if (!this.getPlayer(estado_prueba).hasDied()) // Añado otra acción si no ha muerto con el segundo ACTION_DOWN
                        plan_no_morir.add(Types.ACTIONS.ACTION_DOWN);
                }
            }
            else{
                // ------ TODO: comportamiento reactivo si va a morir por un enemigo 
            }

            hay_que_replanificar = true; // Cuando termine de ejecutar este plan, tendré que replanificar
            System.out.println("Ejecutando acción para no morir: " + plan_no_morir.peekFirst());
            it++;
            return plan_no_morir.pollFirst();
        }
        else if (!bug_morir && accion != Types.ACTIONS.ACTION_NIL){ // No va a morir en el siguiente turno y la acción no es quedarse quieto
            
            // <Choque con rocas>
            // Veo si la acción tiene el resultado esperado o se va a chocar con una roca que está cayendo
            // En ese caso, se queda quieto y la acción que iba a realizar la ejecuta el siguiente turno (si no vuelve a pasar esto)
            // Excepción -> cuando en la casilla siguiente hay una gema o tierra y encima hay una roca, la orientación y posición del jugador
            // no cambian (pero sí es un movimiento válido) (excava la casilla de abajo de la roca / coge la gema)
            
            // También veo si el plan es inválido: el agente lleva más de 15 turnos aplazando acciones por haber chocado con una roca -> replanifico
            boolean hay_gema_o_tierra = false;
            boolean hay_roca = false;
            boolean plan_invalido = false;
            
            if (jugador_sig_estado.getX() == jugador.getX() && jugador_sig_estado.getY() == jugador.getY() &&
                    jugador_sig_estado.getOrientation() == jugador.getOrientation()){ // En el siguiente turno su posición y orientación no han cambiado
                
                ArrayList<Observation>[][] grid = this.getObservationGrid(stateObs);
                int x_jug = jugador.getX();
                int y_jug = jugador.getY();
                
                // Veo si el plan es inválido (lleva más de 15 turnos quieto)
                
                if (it - it_ultimo_movimiento > 15)
                    plan_invalido = true;
                
                
                if (plan_invalido){ // Si el plan es inválido, este turno me quedo quieto y el siguiente replanifico
                    System.out.println("Plan invalido");
                    hay_que_replanificar = true;
                    it++;
                    return Types.ACTIONS.ACTION_NIL;
                }

                
                // Veo si se queda "quieto" porque va a excavar debajo de una roca
                
                
                if (jugador.getOrientation() == Orientation.N){
                    for (Observation obs : grid[x_jug][y_jug-1]){
                        if (obs.getType() == ObservationType.GEM || obs.getType() == ObservationType.GROUND)
                            hay_gema_o_tierra = true;
                    }
                        
                    if(y_jug-2 >= 0){    
                        for (Observation obs : grid[x_jug][y_jug-2]){ // Veo si se queda quieto porque pica para tirar la roca
                            if (obs.getType() == ObservationType.BOULDER)
                                hay_roca = true;
                        }
                    }
                }
                else if (jugador.getOrientation() == Orientation.E){
                   for (Observation obs : grid[x_jug+1][y_jug]){
                        if (obs.getType() == ObservationType.GEM || obs.getType() == ObservationType.GROUND)
                            hay_gema_o_tierra = true;
                    }
                        
                    for (Observation obs : grid[x_jug+1][y_jug-1]){
                            if (obs.getType() == ObservationType.BOULDER)
                                hay_roca = true;
                    }
                }
                else if (jugador.getOrientation() == Orientation.W){
                    for (Observation obs : grid[x_jug-1][y_jug]){
                        if (obs.getType() == ObservationType.GEM || obs.getType() == ObservationType.GROUND)
                            hay_gema_o_tierra = true;
                    }
                    
                    for (Observation obs : grid[x_jug-1][y_jug-1]){
                            if (obs.getType() == ObservationType.BOULDER)
                                hay_roca = true;
                    }
                }
                
                
                if (!hay_gema_o_tierra || !hay_roca){ // Si la acción no es para excavar, se ha chocado -> me quedo quieto
                    System.out.println("Se ha chocado con una roca! - it: " + it);
                    System.out.println("Accion aplazada: " + accion);
                    it++;
                    return Types.ACTIONS.ACTION_NIL; // Me quedo quieto y no ejecuto la acción del plan (la ejecutaré el siguiente turno si no vuelve a pasar)
                }
            }
        }
        
        informacionPlan.plan.removeFirst();
        
        it++;
        return accion;
    }
        
    // Usa el pathFinder para obtener una cota inferior (optimista) de la distancia entre
    // dos casillas (atraviesa las rocas pero no los muros)
    // DA EXCEPCION SI LA POSICION INICIAL O FINAL ESTA SOBRE UN MURO!
    
    private int getHeuristicDistance(int xStart, int yStart, int xGoal, int yGoal){
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
    
    // Sobrecarga de pathExplorer para cuando la posición de inicio es la del 
    // jugador de stateObs
    private PathInformation pathExplorer(int xGoal, int yGoal, StateObservation stateObs){
        return pathExplorer(this.getPlayer(stateObs), xGoal, yGoal, stateObs, null, null);
    }
 
    private PathInformation pathExplorer(PlayerObservation startingPos, int xGoal, int yGoal, StateObservation stateObs,
                                         boolean[][] initialBoulderMap, boolean[][] initialGroundMap) {
        PathInformation plan = new PathInformation();
        PriorityQueue<GridNode> openList = new PriorityQueue<>(
                (GridNode n1, GridNode n2) -> n1.getfCost() - n2.getfCost());
        LinkedList<GridNode> closedList = new LinkedList<>();
        HashSet<GridNode> exploredList = new HashSet<>();

        final ObservationType WALL = ObservationType.WALL;

        ArrayList<Observation>[][] grid = this.getObservationGrid(stateObs);
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
        ArrayList<Observation> obstacles = (ArrayList<Observation>) boulders.clone();
        obstacles.addAll(walls);

        if (initialBoulderMap == null) {
            UtilAlgorithms.initMap(boulderMap, obstacles, XMAX, YMAX);
        } else {
            boulderMap = initialBoulderMap;
        }


        // Create ArrayList containing boulder configurations
        ArrayList<boolean [][]> boulderConfigurations = new ArrayList<>();


        // Ground map
        boolean[][] groundMap = new boolean[XMAX][YMAX];
        ArrayList<Observation> groundList = this.getGroundTilesList(stateObs);

        if (initialGroundMap == null) {
            UtilAlgorithms.initMap(groundMap, groundList, XMAX, YMAX);
        } else {
            groundMap = initialGroundMap;
        }

        // Gems map
        boolean[][] gemsMap = new boolean[XMAX][YMAX];
        ArrayList<Observation> gemsList = this.getGemsList(stateObs);

        UtilAlgorithms.initMap(gemsMap, gemsList, XMAX, YMAX);

        // Simulate initial boulder fall
        UtilAlgorithms.simulateBoulderFall(boulders, boulderMap, groundMap, gemsMap, grid);

        boulderConfigurations.add(boulderMap);

        // Add first node
        openList.add(new GridNode(0, this.getHeuristicDistance(startingPos, goal),
                null, startingPos, startingPos.getOrientation(), 0,
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
                            if ((i == 0) || (nextPosition.getX() == currentNode.getPosition().getX() && currentNode.getForbiAboveGrid())) {
                                forbidAboveGrid = true;
                            }
                        }

                        // Check if the agent is trying to go to the above grid without changing its X position
                        // after moving a boulder above him
                        if ((nextPosition.getX() == currentNode.getPosition().getX() && currentNode.getForbiAboveGrid())) {
                            forbidAboveGrid = true;
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
            plan.groundMap = path.getGroundMap();
            plan.boulderMap = boulderConfigurations.get(path.getBoulderIndex());
            plan = parsePlan(path);
        } else {
            System.out.println("no encontrado");
            plan.existsPath = false;
        }

        plan.searchComplete = true;

        return plan;
    }

    // Sobrecarga de pathExplorer para cuando la posición de inicio es la del
    // jugador de stateObs
    private PathInformation pathExplorer(int xGoal, int yGoal, StateObservation stateObs, ArrayList<Observation> ignoreList){
        return pathExplorer(this.getPlayer(stateObs), xGoal, yGoal, stateObs, ignoreList, null, null);
    }

    // pathExplorer con lista de casillas a ignorar
    private PathInformation pathExplorer(PlayerObservation startingPos, int xGoal, int yGoal, StateObservation stateObs,
                                         ArrayList<Observation> ignoreList, boolean[][] initialBoulderMap, boolean[][] initialGroundMap) {
        PathInformation plan = new PathInformation();
        PriorityQueue<GridNode> openList = new PriorityQueue<>(
                (GridNode n1, GridNode n2) -> n1.getfCost() - n2.getfCost());
        LinkedList<GridNode> closedList = new LinkedList<>();
        HashSet<GridNode> exploredList = new HashSet<>();

        final ObservationType WALL = ObservationType.WALL;

        ArrayList<Observation>[][] grid = this.getObservationGrid(stateObs);
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
        ArrayList<Observation> obstacles = (ArrayList<Observation>) boulders.clone();
        obstacles.addAll(walls);

        if (initialBoulderMap == null) {
            UtilAlgorithms.initMap(boulderMap, obstacles, XMAX, YMAX);
        } else {
            boulderMap = initialBoulderMap;
        }


        // Create ArrayList containing boulder configurations
        ArrayList<boolean [][]> boulderConfigurations = new ArrayList<>();


        // Ground map
        boolean[][] groundMap = new boolean[XMAX][YMAX];
        ArrayList<Observation> groundList = this.getGroundTilesList(stateObs);

        if (initialGroundMap == null) {
            UtilAlgorithms.initMap(groundMap, groundList, XMAX, YMAX);
        } else {
            groundMap = initialGroundMap;
        }

        // Gems map
        boolean[][] gemsMap = new boolean[XMAX][YMAX];
        ArrayList<Observation> gemsList = this.getGemsList(stateObs);

        UtilAlgorithms.initMap(gemsMap, gemsList, XMAX, YMAX);

        // Simulate initial boulder fall
        UtilAlgorithms.simulateBoulderFall(boulders, boulderMap, groundMap, gemsMap, grid);

        boulderConfigurations.add(boulderMap);

        // Add first node
        openList.add(new GridNode(0, this.getHeuristicDistance(startingPos, goal),
                null, startingPos, startingPos.getOrientation(), 0,
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
                            if ((i == 0) || (nextPosition.getX() == currentNode.getPosition().getX() && currentNode.getForbiAboveGrid())) {
                                forbidAboveGrid = true;
                            }
                        }

                        // Check if the agent is trying to go to the above grid without changing its X position
                        // after moving a boulder above him
                        if ((nextPosition.getX() == currentNode.getPosition().getX() && currentNode.getForbiAboveGrid())) {
                            forbidAboveGrid = true;
                        }

                        if (!ignoreList.contains(nextPosition)) {
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
            }

            // Add the current node to the closed list
            closedList.addFirst(currentNode);
        }

        // Get the last explored grid (goal grid)
        GridNode path = closedList.getFirst();

        // Save the path information
        if (foundGoal) {
            plan.groundMap = path.getGroundMap();
            plan.boulderMap = boulderConfigurations.get(path.getBoulderIndex());
            plan = parsePlan(path);
        } else {
            System.out.println("no encontrado");
            plan.existsPath = false;
        }

        plan.searchComplete = true;

        return plan;
    }

    // Sobrecarga de pathExplorer para cuando la posición de inicio es la del
    // jugador de stateObs
    private PathInformation pathExplorer(int xGoal, int yGoal,
                                         StateObservation stateObs, ArrayList<Observation> goalGems,
                                         ElapsedCpuTimer elapsedTimer, long timeThreshold){
        return pathExplorer(this.getPlayer(stateObs), xGoal, yGoal, stateObs, goalGems, elapsedTimer, timeThreshold, null, null);
    } 
    
    private PathInformation pathExplorer(PlayerObservation startingPos, int xGoal, int yGoal,
                                         StateObservation stateObs, ArrayList<Observation> goalGems,
                                         ElapsedCpuTimer elapsedTimer, long timeThreshold,
                                         boolean[][] initialBoulderMap, boolean[][] initialGroundMap) {
        // Creo el objeto que va a guardar la información para el método getHeuristicGems
        // sobre la distancia de las distintas listas de gemas
        mapaCircuitos.clear();

        // Create new plan
        PathInformation plan = new PathInformation();

        // Set up boolean values for found gem and timeout
        boolean foundGoal = false;
        boolean timeout = false;

        // Get game grid
        ArrayList<Observation>[][] grid = this.getObservationGrid(stateObs);

        // Create variables for current node and observation
        GridNode currentNode;
        Observation currentObservation;

        // Set constants like grid size, walls, actions, orientations and goal grid
        final int XMAX = grid.length, YMAX = grid[0].length;
        final ObservationType WALL = ObservationType.WALL;
        final Types.ACTIONS[] actions = {Types.ACTIONS.ACTION_UP, Types.ACTIONS.ACTION_RIGHT, Types.ACTIONS.ACTION_DOWN, Types.ACTIONS.ACTION_LEFT};
        final Orientation[] orientations = {Orientation.N, Orientation.E, Orientation.S, Orientation.W};
        final Observation goal = grid[xGoal][yGoal].get(0);

        // Create data structures that will store the information
        PriorityQueue<GridNode> openList;
        LinkedList<GridNode> closedList;
        HashSet<GridNode> exploredList;
        ArrayList<boolean [][]> boulderConfigurations;
        int exploredStates;


        // If there was no previous information about a search, create new information
        if (searchInfo.isEmpty()) {
            openList = new PriorityQueue<>( (GridNode n1, GridNode n2) -> n1.getfCost() - n2.getfCost() );
            closedList = new LinkedList<>();
            exploredList = new HashSet<>();
            boulderConfigurations = new ArrayList<>();
            exploredStates = 1;


            // Boulder map (contains boulders and walls)
            boolean[][] boulderMap = new boolean[XMAX][YMAX];
            ArrayList<Observation> boulders = this.getBouldersList(stateObs);
            ArrayList<Observation> walls = this.getWallsList(stateObs);
            ArrayList<Observation> obstacles = (ArrayList<Observation>) boulders.clone();
            obstacles.addAll(walls);

            if (initialBoulderMap == null) {
                UtilAlgorithms.initMap(boulderMap, obstacles, XMAX, YMAX);
            } else {
                boulderMap = initialBoulderMap;
            }

            // Ground map
            boolean[][] groundMap = new boolean[XMAX][YMAX];
            ArrayList<Observation> groundList = this.getGroundTilesList(stateObs);

            if (initialGroundMap == null) {
                UtilAlgorithms.initMap(groundMap, groundList, XMAX, YMAX);
            } else {
                groundMap = initialGroundMap;
            }

            // Gems map
            boolean[][] gemsMap = new boolean[XMAX][YMAX];
            ArrayList<Observation> gemsList = this.getGemsList(stateObs);

            UtilAlgorithms.initMap(gemsMap, gemsList, XMAX, YMAX);

            // Simulate initial boulder fall
            UtilAlgorithms.simulateBoulderFall(boulders, boulderMap, groundMap, gemsMap, grid);

            boulderConfigurations.add(boulderMap);

            // Add first node
            openList.add(new GridNode(0, this.getHeuristicDistance(startingPos, goal),
                    null, startingPos, startingPos.getOrientation(), 0,
                    groundMap, gemsMap, false, goalGems.size(), goalGems, null));
        } else {
            // Load previous search information if there was a timeout
            openList = searchInfo.getOpenList();
            closedList = searchInfo.getClosedList();
            exploredList = searchInfo.getExploredList();
            boulderConfigurations = searchInfo.getBoulderConfigurations();
            exploredStates = searchInfo.getExploredStates();
        }

        while (!foundGoal && !openList.isEmpty()) {

            // Check wether there's a timeout
            if (elapsedTimer.remainingTimeMillis() <= timeThreshold) {
                timeout = true;
                break;
            }

            if (exploredStates >= SearchInformation.getMaxStates()) {
                break;
            }

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
            } else if (currentObservation.getX() == xGoal && currentObservation.getY() == yGoal
                        && remainingGems != 0 && exploredStates != 1) {
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

                        // Set the grid ground as digged (false)
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

                            // Find out number of boulders above the current grid
                            // and the index of the highest grid containing a boulder
                            while (newBoulders[x][boulderPos] && !grid[x][boulderPos].get(0).getType().equals(WALL)) {
                                numberBoulders++;
                                boulderPos--;
                            }

                            // Find out the index of the last empty space
                            while (!nextGround[x][emptyPos] && (!grid[x][emptyPos].get(0).getType().equals(WALL) && !nextGemsMap[x][emptyPos] && !currentBoulders[x][emptyPos])) {
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
                            if ((i == 0) || (nextPosition.getX() == currentNode.getPosition().getX() && currentNode.getForbiAboveGrid())) {
                                forbidAboveGrid = true;
                            }

                        }

                        // Compute the heuristic value (h)
                        int heuristic;

                        if (nextRemainingGems > 0) {
                            heuristic = this.getHeuristicGems(nextPosition, goal, nextGemsList);
                        } else {
                            heuristic = this.getHeuristicDistance(nextPosition, goal);
                        }

                        // Check if the agent is trying to go to the above grid without changing its X position
                        // after moving a boulder above him
                        if ((nextPosition.getX() == currentNode.getPosition().getX() && currentNode.getForbiAboveGrid())) {
                            forbidAboveGrid = true;
                        }

                        // Create new grid node
                        GridNode node = new GridNode(currentNode.getgCost() + numberActions,
                                heuristic,
                                actionList, nextPosition, orientations[i], bouldIndx,
                                nextGround, nextGemsMap, forbidAboveGrid, nextRemainingGems, nextGemsList, currentNode);

                        // Add the node to the explored list
                        if (exploredList.add(node)) {
                            exploredStates++;
                            openList.add(node);
                        }
                    }
                }
            }

            // Add the current node to the closed list
            closedList.addFirst(currentNode);
        }

        // Check wether there's a timeout
        if (timeout) {
            searchInfo = new SearchInformation(openList, closedList, exploredList, boulderConfigurations, exploredStates);
            plan.plan.add(Types.ACTIONS.ACTION_NIL);
            return plan;
        }


        // Save the path information
        if (foundGoal) {
            // Get the last explored grid (goal grid)
            GridNode path = closedList.getFirst();
            plan.groundMap = path.getGroundMap();
            plan.boulderMap = boulderConfigurations.get(path.getBoulderIndex());
            plan = parsePlan(path);
        } else {
            System.out.println("no encontrado " + exploredStates);
            plan.existsPath = false;
        }

        plan.searchComplete = true;

        searchInfo = new SearchInformation();
        return plan;
    }

    // Sobrecarga de pathExplorer para cuando la posición de inicio es la del
    // jugador de stateObs
    private PathInformation pathExplorer(int xGoal, int yGoal,
                                         StateObservation stateObs, ArrayList<Observation> goalGems,
                                         ElapsedCpuTimer elapsedTimer, long timeThreshold, ArrayList<Observation> ignoreList){
        return pathExplorer(this.getPlayer(stateObs), xGoal, yGoal, stateObs, goalGems, elapsedTimer, timeThreshold, ignoreList, null, null);
    }

    private PathInformation pathExplorer(PlayerObservation startingPos, int xGoal, int yGoal,
                                         StateObservation stateObs, ArrayList<Observation> goalGems,
                                         ElapsedCpuTimer elapsedTimer, long timeThreshold,
                                         ArrayList<Observation> ignoreList,
                                         boolean[][] initialBoulderMap, boolean[][] initialGroundMap) {
        // Creo el objeto que va a guardar la información para el método getHeuristicGems
        // sobre la distancia de las distintas listas de gemas
        mapaCircuitos.clear();

        // Create new plan
        PathInformation plan = new PathInformation();

        // Set up boolean values for found gem and timeout
        boolean foundGoal = false;
        boolean timeout = false;

        // Get game grid
        ArrayList<Observation>[][] grid = this.getObservationGrid(stateObs);

        // Create variables for current node and observation
        GridNode currentNode;
        Observation currentObservation;

        // Set constants like grid size, walls, actions, orientations and goal grid
        final int XMAX = grid.length, YMAX = grid[0].length;
        final ObservationType WALL = ObservationType.WALL;
        final Types.ACTIONS[] actions = {Types.ACTIONS.ACTION_UP, Types.ACTIONS.ACTION_RIGHT, Types.ACTIONS.ACTION_DOWN, Types.ACTIONS.ACTION_LEFT};
        final Orientation[] orientations = {Orientation.N, Orientation.E, Orientation.S, Orientation.W};
        final Observation goal = grid[xGoal][yGoal].get(0);

        // Create data structures that will store the information
        PriorityQueue<GridNode> openList;
        LinkedList<GridNode> closedList;
        HashSet<GridNode> exploredList;
        ArrayList<boolean [][]> boulderConfigurations;
        int exploredStates;


        // If there was no previous information about a search, create new information
        if (searchInfo.isEmpty()) {
            openList = new PriorityQueue<>( (GridNode n1, GridNode n2) -> n1.getfCost() - n2.getfCost() );
            closedList = new LinkedList<>();
            exploredList = new HashSet<>();
            boulderConfigurations = new ArrayList<>();
            exploredStates = 1;


            // Boulder map (contains boulders and walls)
            boolean[][] boulderMap = new boolean[XMAX][YMAX];
            ArrayList<Observation> boulders = this.getBouldersList(stateObs);
            ArrayList<Observation> walls = this.getWallsList(stateObs);
            ArrayList<Observation> obstacles = (ArrayList<Observation>) boulders.clone();
            obstacles.addAll(walls);

            if (initialBoulderMap == null) {
                UtilAlgorithms.initMap(boulderMap, obstacles, XMAX, YMAX);
            } else {
                boulderMap = initialBoulderMap;
            }

            // Ground map
            boolean[][] groundMap = new boolean[XMAX][YMAX];
            ArrayList<Observation> groundList = this.getGroundTilesList(stateObs);

            if (initialGroundMap == null) {
                UtilAlgorithms.initMap(groundMap, groundList, XMAX, YMAX);
            } else {
                groundMap = initialGroundMap;
            }

            // Gems map
            boolean[][] gemsMap = new boolean[XMAX][YMAX];
            ArrayList<Observation> gemsList = this.getGemsList(stateObs);

            UtilAlgorithms.initMap(gemsMap, gemsList, XMAX, YMAX);

            // Simulate initial boulder fall
            UtilAlgorithms.simulateBoulderFall(boulders, boulderMap, groundMap, gemsMap, grid);

            boulderConfigurations.add(boulderMap);

            // Add first node
            openList.add(new GridNode(0, this.getHeuristicDistance(startingPos, goal),
                    null, startingPos, startingPos.getOrientation(), 0,
                    groundMap, gemsMap, false, goalGems.size(), goalGems, null));
        } else {
            // Load previous search information if there was a timeout
            openList = searchInfo.getOpenList();
            closedList = searchInfo.getClosedList();
            exploredList = searchInfo.getExploredList();
            boulderConfigurations = searchInfo.getBoulderConfigurations();
            exploredStates = searchInfo.getExploredStates();
        }

        while (!foundGoal && !openList.isEmpty()) {

            // Check wether there's a timeout
            if (elapsedTimer.remainingTimeMillis() <= timeThreshold) {
                timeout = true;
                break;
            }

            if (exploredStates >= SearchInformation.getMaxStates()) {
                break;
            }

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
            } else if (currentObservation.getX() == xGoal && currentObservation.getY() == yGoal
                    && remainingGems != 0 && exploredStates != 1) {
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

                        // Set the grid ground as digged (false)
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

                            // Find out number of boulders above the current grid
                            // and the index of the highest grid containing a boulder
                            while (newBoulders[x][boulderPos] && !grid[x][boulderPos].get(0).getType().equals(WALL)) {
                                numberBoulders++;
                                boulderPos--;
                            }

                            // Find out the index of the last empty space
                            while (!nextGround[x][emptyPos] && (!grid[x][emptyPos].get(0).getType().equals(WALL) && !nextGemsMap[x][emptyPos] && !currentBoulders[x][emptyPos])) {
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
                            if ((i == 0) || (nextPosition.getX() == currentNode.getPosition().getX() && currentNode.getForbiAboveGrid())) {
                                forbidAboveGrid = true;
                            }

                        }

                        // Compute the heuristic value (h)
                        int heuristic;

                        if (nextRemainingGems > 0) {
                            heuristic = this.getHeuristicGems(nextPosition, goal, nextGemsList);
                        } else {
                            heuristic = this.getHeuristicDistance(nextPosition, goal);
                        }

                        // Check if the agent is trying to go to the above grid without changing its X position
                        // after moving a boulder above him
                        if ((nextPosition.getX() == currentNode.getPosition().getX() && currentNode.getForbiAboveGrid())) {
                            forbidAboveGrid = true;
                        }

                        // Check if next position is in ignore list
                        if (!ignoreList.contains(nextPosition)) {
                            // Create new grid node
                            GridNode node = new GridNode(currentNode.getgCost() + numberActions,
                                    heuristic,
                                    actionList, nextPosition, orientations[i], bouldIndx,
                                    nextGround, nextGemsMap, forbidAboveGrid, nextRemainingGems, nextGemsList, currentNode);

                            // Add the node to the explored list
                            if (exploredList.add(node)) {
                                exploredStates++;
                                openList.add(node);
                            }
                        }
                    }
                }
            }

            // Add the current node to the closed list
            closedList.addFirst(currentNode);
        }

        // Check wether there's a timeout
        if (timeout) {
            searchInfo = new SearchInformation(openList, closedList, exploredList, boulderConfigurations, exploredStates);
            plan.plan.add(Types.ACTIONS.ACTION_NIL);
            return plan;
        }


        // Save the path information
        if (foundGoal) {
            // Get the last explored grid (goal grid)
            GridNode path = closedList.getFirst();
            plan.groundMap = path.getGroundMap();
            plan.boulderMap = boulderConfigurations.get(path.getBoulderIndex());
            plan = parsePlan(path);
        } else {
            System.out.println("no encontrado " + exploredStates);
            plan.existsPath = false;
        }

        plan.searchComplete = true;

        searchInfo = new SearchInformation();
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

        plan.existsPath = true;

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
        final float alfa = 2f; // Valor por el que se multiplica el valor de la heurística: pierde admisibilidad y monotonía pero es más eficiente
        
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
                                                            
        return (int)(alfa*total_dist);
    }
    
    // Obtiene las distancias aproximadas entre cada pareja de clusters usando una matriz de distancias
    // Se calcula primero para cada pareja de clusters las dos gemas más cercanas
    // entre sí usando la distancia Manhattan y después, con el pathfinder
    // se calcula la distancia entre esos dos clusters
    
    // Guarda la matriz de distancias dentro del objeto clust_inf, de la clase Cluster Information
    // Se ha creado como método de esta clase en vez de ClusterInformation para así poder
    // usar getHeuristicDistance
    private void saveClustersDistances(ClusterInformation clust_inf){   
        int num_clusters = clust_inf.clusters.size();
        int[][] matriz_dist = new int[num_clusters][num_clusters]; // Valor inicial -> 0
        Cluster cluster1, cluster2;
        int num_gems_1, num_gems_2;
        Observation gem_act, gem_act_2;
        int x_act, y_act;
        int min_dist, ind_gem_1 = -1, ind_gem_2 = -1, this_dist;
        Observation gem1, gem2;
        int heuristic_dist;
        PathInformation plan;
        
        // d(a,b) = d(b,a) por lo que solo recorro la diagonal inferior de la matriz
        for (int i = 1; i < num_clusters; i++)
            for (int j = 0; j < i; j++){
                // Calculo la pareja de gemas más cercanas (según el getHeuristicDistance) para esos dos clusters 
                cluster1 = clust_inf.clusters.get(i);
                cluster2 = clust_inf.clusters.get(j);
                
                num_gems_1 = cluster1.getNumGems();
                num_gems_2 = cluster2.getNumGems();
                
                min_dist = 1000;
                
                for (int k = 0; k < num_gems_1; k++){
                    gem_act = cluster1.getGem(k);
                    x_act = gem_act.getX();
                    y_act = gem_act.getY();
                    
                    for (int l = 0; l < num_gems_2; l++){
                        gem_act_2 = cluster2.getGem(l);
                        this_dist = getHeuristicDistance(x_act, y_act, gem_act_2.getX(), gem_act_2.getY());
                        
                        // Valdrá -1 si no existe camino
                        if (this_dist != -1 && this_dist < min_dist){
                            min_dist = this_dist;
                            ind_gem_1 = k;
                            ind_gem_2 = l;
                        }
                    }
                }
                 
                // Calculo usando getHeuristicDistance la distancia entre esas dos gemas
                
                gem1 = cluster1.getGem(ind_gem_1);
                gem2 = cluster2.getGem(ind_gem_2);
                heuristic_dist = getHeuristicDistance(gem1.getX(), gem1.getY(), gem2.getX(), gem2.getY());
                             
                matriz_dist[i][j] = matriz_dist[j][i] = heuristic_dist; // Guardo en la matriz esa distancia como la distancia entre los 2 clústeres
            }
        
        clust_inf.matriz_dist = matriz_dist; // Guardo la matriz de distancias en clust_inf
    }

    // Crea el mejor circuito entre los clústeres de clust_inf y lo guarda dentro de este objeto
    // Start se corresponde con la casilla de inicio del circuito (generalmente la posición del jugador)
    // y Goal con la casilla de destino (generalmente la salida del nivel)
    private void saveCircuit(ClusterInformation clust_inf, int xStart, int yStart, int xGoal, int yGoal){
        // Creo los 2 vectores de distancias desde Start y Goal a los clústeres de clust_inf
        int num_clusters = clust_inf.getNumClusters();
        
        int[] distClusterStart = new int[num_clusters];
        int[] distClusterGoal = new int[num_clusters];
        
        Cluster cluster_act;
        Observation gem_act;
        int num_gems;
        int min_dist_start;
        int min_dist_goal;
        int x_act, y_act;
        int this_dist_start;
        int this_dist_goal;
        int ind_gem_start = -1;
        int ind_gem_goal = -1;
        Observation gem_goal, gem_start;
        
        for (int i = 0; i < num_clusters; i++){
            // Calculo la pareja de gemas más cercanas (según el getHeuristicDistance) para esos dos clusters 
            cluster_act = clust_inf.clusters.get(i);
                
            num_gems = cluster_act.getNumGems();
                
            min_dist_start = 1000;
            min_dist_goal = 1000;
                
            for (int k = 0; k < num_gems; k++){
                gem_act = cluster_act.getGem(k);
                x_act = gem_act.getX();
                y_act = gem_act.getY();
                    
                this_dist_start = getHeuristicDistance(x_act, y_act, xStart, yStart);
                this_dist_goal = getHeuristicDistance(x_act, y_act, xGoal, yGoal);
                        
                // Valdrá -1 si no existe camino
                if (this_dist_start != -1 && this_dist_start < min_dist_start){
                    min_dist_start = this_dist_start;
                    ind_gem_start = k;
                }

                if (this_dist_goal != -1 && this_dist_goal < min_dist_goal){
                    min_dist_goal = this_dist_goal;
                    ind_gem_goal = k;
                }
            }
                 
            // Calculo usando getHeuristicDistance la distancia entre esas gemas con Start y Goal    
            gem_goal = cluster_act.getGem(ind_gem_goal);
            gem_start = cluster_act.getGem(ind_gem_start);
            
            distClusterStart[i] = getHeuristicDistance(gem_start.getX(), gem_start.getY(), xStart, yStart);
            distClusterGoal[i] = getHeuristicDistance(gem_goal.getX(), gem_goal.getY(), xGoal, yGoal);
        }
        
        // Llamo al método createCircuit de clusterInformation
        clust_inf.createCircuit(xStart, yStart, xGoal, yGoal, distClusterStart, distClusterGoal);
    }

    // Se tiene que llamar después de saveCircuit
    // No es método de ClusterInformation porque quiero usar getHeuristicDistance
    // Guarda en clust_inf las gemas que funcionan como nodos del circuito
    // Estos nodos (gemas) serán los puntos exactos que unirán los distintos
    // clústeres. El pathExplorer sencillo (sin coger gemas) se lanzará
    // entre el nodo de salida de un clúster y el nodo de entrada del siguiente clúster
    // Algoritmo: voy recorriendo los clústeres del circuito y para cada pareja
    // de clústeres contiguos (en el circuito) calculo la pareja de gemas más cercanas
    // La gema del clúster i será el nodo de salida del clúster i y la gema del clúster
    // i+1 en el circuito será el nodo de entrada del clúster i+1. Todos los clústeres
    // tienen un nodo de salida y otro de entrada. El nodo de entrada del primer clúster
    // se conecta con la casilla Start y el nodo de salida del último clúster con
    // xGoal, yGoal
    
    private void saveCircuitNodes(ClusterInformation clust_inf, int xStart, int yStart, int xGoal, int yGoal){
        ArrayList<Observation> gems;
        int num_gems;
        Observation gem_act;
        int dist;
        int min_dist;
        int ind_min = -1;
        
        // Unión de Start con el primer clúster -> elijo la gema del primer clúster más cercana a start
        gems = clust_inf.getGemsCircuitCluster(0);
        min_dist = 10000;
        num_gems = gems.size();
        
        for (int i = 0; i < num_gems; i++){ // Recorro las gemas del primer clúster del circuito
            gem_act = gems.get(i);
            dist = this.getHeuristicDistance(xStart, yStart, gem_act.getX(), gem_act.getY());
            
            if (dist < min_dist){
                min_dist = dist;
                ind_min = i;
            }
        }
        
        clust_inf.nodos_circuito = new ArrayList<>();
        clust_inf.nodos_circuito.add(new Integer(ind_min));
        
        // Uniones de cada clúster con el siguiente, empezando por el clúster 0 -> parejas nodo_salida, nodo_entrada
        int ind_min_1 = -1; // Indices del par de gemas más cercanas
        int ind_min_2 = -1;
        ArrayList<Observation> gems_1;
        ArrayList<Observation> gems_2;
        int num_gems_1;
        int num_gems_2;
        
        
        for (int k = 0; k < clust_inf.circuito.size()-1; k++){ // Recorro cada pareja de clústeres -> parejas: k con k+1
            gems_1 = clust_inf.getGemsCircuitCluster(k);
            gems_2 = clust_inf.getGemsCircuitCluster(k+1);
            num_gems_1 = gems_1.size();
            num_gems_2 = gems_2.size();
            ind_min_1 = 0; // Intento no repetir gemas, pero si no hay más remedio (clúster de 1 sola gema), se escoge esa
            min_dist = 10000;
            Observation gem_1_act;
            
            if (num_gems_1 == 1){ // Si solo hay una gema tengo que escoger esa
                for (int i = 0; i < num_gems_1; i++){ // Recorro las gemas del primer clúster
                    gem_1_act = gems_1.get(i);

                    for (int j = 0; j < num_gems_2; j++){ // Recorro las gemas del segundo clúster
                        dist = this.getHeuristicDistance(gem_1_act.getX(), gem_1_act.getY(),
                                gems_2.get(j).getX(), gems_2.get(j).getY());

                        if (dist < min_dist){
                            min_dist = dist;
                            ind_min_2 = j;
                            ind_min_1 = i;
                        }
                    }
                }
            }
            else{
                int ultimo_valor = clust_inf.nodos_circuito.get(clust_inf.nodos_circuito.size()-1);
                
                for (int i = 0; i < num_gems_1; i++){ // Recorro las gemas del primer clúster
                    if (i != ultimo_valor){    
                        gem_1_act = gems_1.get(i);

                        for (int j = 0; j < num_gems_2; j++){ // Recorro las gemas del segundo clúster
                            dist = this.getHeuristicDistance(gem_1_act.getX(), gem_1_act.getY(),
                                    gems_2.get(j).getX(), gems_2.get(j).getY());

                            if (dist < min_dist){
                                min_dist = dist;
                                ind_min_2 = j;
                                ind_min_1 = i;
                            }
                        }
                    }
                }   
            }
            
            clust_inf.nodos_circuito.add(ind_min_1);
            clust_inf.nodos_circuito.add(ind_min_2);
        }
        
        // Por último añado el nodo de salida del último clúster: lo uno con la casilla Goal
        gems = clust_inf.getGemsCircuitCluster(clust_inf.circuito.size()-1); // obtengo las gemas del último clúster
        min_dist = 10000;
        num_gems = gems.size();
        
        for (int i = 0; i < num_gems; i++){ // Recorro las gemas del último clúster
            gem_act = gems.get(i);
            dist = this.getHeuristicDistance(xGoal, yGoal, gem_act.getX(), gem_act.getY());
            
            if (dist < min_dist){
                min_dist = dist;
                ind_min = i;
            }
        }
        
        clust_inf.nodos_circuito.add(new Integer(ind_min));
    }
    
    // Este método transforma el plan dado por el A* sencillo para ir a la gema que funciona como nodo de entrada
    // del siguiente clúster y devuelve el mismo plan pero solo con las acciones antes de mover una roca.
    // Es decir, si la acción i mueve una roca, devuelve el plan con las acciones [0, i-1]
    // x_ini, y_ini -> posición desde donde se empezará a aplicar el plan
    
    // VER SI LO ELIMINO
    
    /*
    private LinkedList<Types.ACTIONS> prunePlan(StateObservation stateObs, LinkedList<Types.ACTIONS> plan_inicial, int x_ini, int y_ini, Orientation or_ini){
        LinkedList<Types.ACTIONS> plan_podado;
        int x_act = x_ini;
        int y_act = y_ini;
        Orientation or_act = or_ini;
        Types.ACTIONS sig_acc;
        int num_acciones = plan_inicial.size();
        int i;
        
        for (i = 0; i < num_acciones; i++){ // Recorro el plan hasta que encuentre una acción que mueva una roca
            sig_acc = plan_inicial.get(i);
            
        }
         
        
        return plan_inicial;
    }*/
    
    // Dados dos clústeres, devuelve un punto intermedio entre los dos que sea lo más "seguro" posible
    // Es decir, que si existe camino se pueda llegar a él
    // Algoritmo: busca un punto cercano a la casilla intermedia entre las dos gemas más cercanas de los
    // dos clústeres. Esta casilla no puede tener ninguna gema a la derecha, izquierda, arriba ni abajo, ni tampoco ningún enemigo en esas casillas
    // Si ese punto no existe, se devuelve -1, -1
    // Aparte ese punto no puede ser un muro (aunque sí puede tener muros alrededor)
    
    // Da error si se llama y los dos clusters son iguales!!
    
    private int[] getPuntoIntermedioClusters(ArrayList<Observation> gems_1, ArrayList<Observation>  gems_2, StateObservation stateObs){
        int[] casilla = new int[2];
        casilla[0] = -1;
        casilla[1] = -1;
        
        // Calculo la pareja de gemas más cercanas
        int num_gems_1 = gems_1.size();
        int num_gems_2 = gems_2.size();
        int min_dist = 10000;
        int ind_1 = -1;
        int ind_2 = -1;
        int dist;
        Observation this_gem;
        
        for (int i = 0; i < num_gems_1; i++){
            this_gem = gems_1.get(i);
            
            for (int j = 0; j < num_gems_2; j++){
                dist = this.getHeuristicDistance(this_gem, gems_2.get(j));
                
                if (dist < min_dist){
                    min_dist = dist;
                    ind_1 = i;
                    ind_2 = j;
                }   
            }
        }
        
        int x_1 = gems_1.get(ind_1).getX();
        int y_1 = gems_1.get(ind_1).getY();
        int x_2 = gems_2.get(ind_2).getX();
        int y_2 = gems_2.get(ind_2).getY();
        
        int x_centro = (x_1 + x_2) / 2; // Empiezo en el punto intermedio entre las dos gemas más cercanas
        int y_centro = (y_1 + y_2) / 2;
        
        ArrayList<Observation>[][] grid = this.getObservationGrid(stateObs);
        int ancho_grid = grid.length;
        int alto_grid = grid[0].length;
        
        boolean[][] matriz_rocas = new boolean[ancho_grid][alto_grid]; // Guardo las rocas en una matriz de booleanos
        boolean[][] matriz_muros = new boolean[ancho_grid][alto_grid]; // Guardo los muros en una matriz de booleanos
        boolean[][] matriz_enemigos = new boolean[ancho_grid][alto_grid]; // Guardo los enemigos en una matriz de booleanos
        
        for (int x = 0; x < ancho_grid; x++)
            for (int y = 0; y < alto_grid; y++){
                matriz_rocas[x][y] = false;
                matriz_muros[x][y] = false;
                matriz_enemigos[x][y] = false;
                
                for (Observation obs : grid[x][y]){
                    if (obs.getType() == ObservationType.BOULDER)
                        matriz_rocas[x][y] = true;
                    else if (obs.getType() == ObservationType.WALL)
                        matriz_muros[x][y] = true;
                    else if (obs.getType() == ObservationType.BAT)
                        matriz_enemigos[x][y] = true;
                    else if (obs.getType() == ObservationType.SCORPION)
                        matriz_enemigos[x][y] = true;
                }
            }
        
        
        // Exploro esa casilla y las que están en un radio de +3,-3 en x e y
         boolean casilla_encontrada = false;
        
        if (matriz_rocas[x_centro][y_centro] == false && matriz_muros[x_centro][y_centro] == false && matriz_enemigos[x_centro][y_centro] == false
                        && matriz_rocas[x_centro+1][y_centro] == false && matriz_enemigos[x_centro+1][y_centro] == false
                        && matriz_rocas[x_centro-1][y_centro] == false && matriz_enemigos[x_centro-1][y_centro] == false
                        && matriz_rocas[x_centro][y_centro+1] == false && matriz_enemigos[x_centro][y_centro+1] == false
                        && matriz_rocas[x_centro][y_centro-1] == false && matriz_enemigos[x_centro][y_centro-1] == false){
            casilla[0] = x_centro;
            casilla[1] = y_centro;
            casilla_encontrada = true;
            }
        
        int this_x, this_y;
        
        for (int x_add = 1; x_add <= 3 && !casilla_encontrada; x_add++)
            for (int y_add = 1; y_add <= 3 && !casilla_encontrada; y_add++){
                
                // Exploro las 8 casillas dadas por +-x_add y +-y_add
                for (int sig_x = -1; sig_x <= 1 && !casilla_encontrada; sig_x++) // Signo de x
                    for (int sig_y = -1; sig_y <=1 && !casilla_encontrada; sig_y++){ // Signo de y
                        
                        if (sig_x != 0 || sig_y != 0){ // No puede ser la casilla central
                            this_x = x_centro + x_add*sig_x;
                            this_y = y_centro + y_add*sig_y;
                            
                            
                            if (matriz_rocas[this_x][this_y] == false && matriz_muros[this_x][this_y] == false && matriz_enemigos[this_x][this_y] == false
                                && matriz_rocas[this_x+1][this_y] == false && matriz_enemigos[this_x+1][this_y] == false
                                && matriz_rocas[this_x-1][this_y] == false && matriz_enemigos[this_x-1][this_y] == false
                                && matriz_rocas[this_x][this_y+1] == false && matriz_enemigos[this_x][this_y+1] == false
                                && matriz_rocas[this_x][this_y-1] == false && matriz_enemigos[this_x][this_y-1] == false){
                                
                                casilla[0] = this_x;
                                casilla[1] = this_y;
                                casilla_encontrada = true;
                            }
                        }
                    } 
            }
        
        return casilla;
    }
}
