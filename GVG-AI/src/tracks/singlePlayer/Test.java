package tracks.singlePlayer;

import java.util.Random;

import core.logging.Logger;
import tools.Utils;
import tracks.ArcadeMachine;

/**
 * Created with IntelliJ IDEA. User: Diego Date: 04/10/13 Time: 16:29 This is a
 * Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Test {

    public static void main(String[] args) {

		// Available tracks:
		String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
		String doNothingController = "tracks.singlePlayer.simple.doNothing.Agent";
		String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
		String sampleFlatMCTSController = "tracks.singlePlayer.simple.greedyTreeSearch.Agent";

		String sampleMCTSController = "tracks.singlePlayer.advanced.sampleMCTS.Agent";
                String sampleRSController = "tracks.singlePlayer.advanced.sampleRS.Agent";
                String sampleRHEAController = "tracks.singlePlayer.advanced.sampleRHEA.Agent";
		String sampleOLETSController = "tracks.singlePlayer.advanced.olets.Agent";
                
                // Mi controlador
                String miControlador = "practica_planificacion.TestAgent";

		//Load available games
		String spGamesCollection =  "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

		//Game settings
		boolean visuals = true;
		int seed = new Random().nextInt();

                // Juegos: 1 -> evitar que se quemen las personas al pasar
                // 4 -> Resolver puzle y encontrar la llave
                // 5 -> conseguir que las cintas transportadoras lleven el cofre al objetivo
                // <10> -> conseguir x diamantes e ir a la salida sin que te maten
                // 11 -> versión diferente en la que no pueden excavar los enemigos
                // 12 -> puzle empujando una llave
                // <21> -> sokoban (puzzle)
                // 31 -> puzzle -> abrir cada puerta moviendo (empujando) el bloque hasta una casilla especial
                // 36 -> puzzle -> llegar al objetivo empujando bloques a agujeros para que desaparezcan
                // 38 -> puzzle -> llegar al objetivo quemando bloques con disparos que se recargan recogiendo gemas
                // 41 -> cruzar la calle evitando coches
                // 42 -> parecido al anterior
                // <43> -> como el snake, pero no paras de crecer y tienes que intentar comer lo más rápido toda la comida
                // <63> -> comecocos
                // 68 -> llegar hasta la salida, esquivando los disparos y usando portales
                // 100 -> llegar hasta la salida esquivando a los enemigos o matándolos
                
                // Juego elegido -> el 11 (y también el 10)
                
                boolean juego_yo = true;
                
		// Game and level to play
		int gameIdx = 11; // 39 para el de los incendios
		int levelIdx = 0; // level names from 0 to 4 (game_lvlN.txt).
		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];
		String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);

		String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"
						// + levelIdx + "_" + seed + ".txt";
						// where to record the actions
						// executed. null if not to save.

		// 1. This starts a game, in a level, played by a human.
                if (juego_yo)
		ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);

		// 2. This plays a game in a level by the controller.
                if (!juego_yo)
                ArcadeMachine.runOneGame(game, level1, visuals, miControlador, recordActionsFile, seed, 0);

                
		// 3. This replays a game from an action file previously recorded
	//	 String readActionsFile = recordActionsFile;
	//	 ArcadeMachine.replayGame(game, level1, visuals, readActionsFile);

		// 4. This plays a single game, in N levels, M times :
//		String level2 = new String(game).replace(gameName, gameName + "_lvl" + 1);
//		int M = 10;
//		for(int i=0; i<games.length; i++){
//			game = games[i][0];
//			gameName = games[i][1];
//			level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
//			ArcadeMachine.runGames(game, new String[]{level1}, M, sampleMCTSController, null);
//		}

		//5. This plays N games, in the first L levels, M times each. Actions to file optional (set saveActions to true).
//		int N = games.length, L = 2, M = 1;
//		boolean saveActions = false;
//		String[] levels = new String[L];
//		String[] actionFiles = new String[L*M];
//		for(int i = 0; i < N; ++i)
//		{
//			int actionIdx = 0;
//			game = games[i][0];
//			gameName = games[i][1];
//			for(int j = 0; j < L; ++j){
//				levels[j] = game.replace(gameName, gameName + "_lvl" + j);
//				if(saveActions) for(int k = 0; k < M; ++k)
//				actionFiles[actionIdx++] = "actions_game_" + i + "_level_" + j + "_" + k + ".txt";
//			}
//			ArcadeMachine.runGames(game, levels, M, sampleRHEAController, saveActions? actionFiles:null);
//		}


    }
}
