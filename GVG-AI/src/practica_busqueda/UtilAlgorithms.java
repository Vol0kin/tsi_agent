package practica_busqueda;

import java.util.ArrayList;

public interface UtilAlgorithms {

    static void initMap(boolean[][] map, ArrayList<Observation> information, int xSize, int ySize) {

        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                map[x][y] = false;
            }
        }

        for (Observation info: information) {
            map[info.getX()][info.getY()] = true;
        }
    }

    static void copy2DArray(boolean[][] source, boolean[][] destination, int xSize, int ySize) {

        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                destination[x][y] = source[x][y];
            }
        }
    }
}
