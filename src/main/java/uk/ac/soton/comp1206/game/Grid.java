package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

import java.util.HashSet;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 *
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 *
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 *
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

    /**
     * The number of columns in this grid
     */
    private final int cols;

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;

    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];

        //Add a SimpleIntegerProperty to every block in the grid
        for(var y = 0; y < rows; y++) {
            for(var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     * @param x column
     * @param y row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * returns true if GamePiece gp can be placed
     * @param x: row
     * @param y: column
     * @param gp: GamePiece
     * @return
     */
    boolean canPlayPiece(int x, int y, GamePiece gp) {
        int[][] piece = gp.getBlocks();
        int p = piece.length;
        int g = getCols();
        System.out.println(" x, y ="+x+","+y);
        int offset = p / 2; // integer division
        for (int i = 0; i < p; i++)
            for (int j = 0; j < p; j++)
                if (piece[i][j] > 0 && get(i + x - offset, j + y - offset) > 0)
                    return false;
        return true;
    }

    boolean playPiece(int x, int y, GamePiece gp){
        if (!canPlayPiece(x, y, gp)) return false;
        int[][] piece = gp.getBlocks();
        int offset = piece.length/2;
        for (int i = 0; i < piece.length; i++)
            for (int j = 0; j < piece.length; j++) {
                if (piece[i][j] > 0)
                    set(i+x-offset, j+y-offset, piece[i][j]);
            }
        return true;
    }
//    public int afterPiece() {
//        HashSet<GameBlockCoordinate> set = new HashSet<>();
//        // find all complete rows
//        int counter = 0;
//        for (int i = 0; i < getRows(); i++) {
//            boolean complete = true;
//            for (int j = 0; j < getCols(); j++) {
//                if (get(i, j) == 0) {
//                    complete = false;
//                }
//            }
//            if (complete) { // add all to set to zero
//                counter ++;
//                for (int j = 0; j < getCols(); j++)
//                    set.add(new GameBlockCoordinate(i, j));
//            }
//        }
//        // find all complete columns
//        for (int j = 0; j < getRows(); j++) {
//            boolean complete = true;
//            for (int i = 0; i < getCols(); i++) {
//                if (get(i, j) == 0) {
//                    complete = false;
//                }
//            }
//            if (complete) { // add all to set to zero
//                counter ++;
//                for (int i = 0; i < getCols(); i++)
//                    set.add(new GameBlockCoordinate(i, j));
//            }
//        }
//
//        // zero all complete
//        for (GameBlockCoordinate g: set) {
//            set(g.getX(), g.getY(), 0);
//        }
//        return counter*100;
//    }
}