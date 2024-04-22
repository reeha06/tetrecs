package uk.ac.soton.comp1206.game;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;

import java.util.HashSet;
import java.util.Random;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);
    private NextPieceListener nextPieceListener;
    private LineClearedListener lineClearedListener;
    private Random rand = new Random();
    private GamePiece currentPiece = spawnPiece();
    private GamePiece followingPiece = spawnPiece();
    private SimpleIntegerProperty lives = new SimpleIntegerProperty(3);
    private SimpleIntegerProperty level = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty score = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty multiplier = new SimpleIntegerProperty(1);
    //public SimpleStringProperty piece = new SimpleStringProperty();
    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        nextPiece();
        logger.info("Initialising game");
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
//        //Get the new value for this block
//        int previousValue = grid.get(x,y);
//        int newValue = previousValue + 1;
//        if (newValue  > GamePiece.PIECES) {
//            newValue = 0;
//        }
//
//        //Update the grid with the new value
//        grid.set(x,y,newValue);

        // get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        if (grid.playPiece(x, y, currentPiece)) {
            nextPiece();
        }
        //grid.playPiece(x, y, currentPiece);
        int s = afterPiece();
        score.set(score.get()+s);
        setLevel();
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
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

    public int afterPiece() {
        HashSet<GameBlockCoordinate> set = new HashSet<>();
        // find all complete rows
        int counter = 0;
        for (int i = 0; i < getRows(); i++) {
            boolean complete = true;
            for (int j = 0; j < getCols(); j++) {
                if (grid.get(i, j) == 0) {
                    complete = false;
                }
            }
            if (complete) { // add all to set to zero
                counter ++;
                for (int j = 0; j < getCols(); j++)
                    set.add(new GameBlockCoordinate(i, j));
            }
        }
        // find all complete columns
        for (int j = 0; j < getRows(); j++) {
            boolean complete = true;
            for (int i = 0; i < getCols(); i++) {
                if (grid.get(i, j) == 0) {
                    complete = false;
                }
            }
            if (complete) { // add all to set to zero
                counter ++;
                for (int i = 0; i < getCols(); i++)
                    set.add(new GameBlockCoordinate(i, j));
            }
        }
        lineClearedListener.onLineCleared(set);
        // zero all complete
        for (GameBlockCoordinate g: set) {
            grid.set(g.getX(), g.getY(), 0);
        }
        // if counter >= 1 then increase mulitplier by one otherwise put multiplier to 1 again
        if (counter >= 1) {
            increaseMulitplier();
            Multimedia.playSound("clear.wav");
        } else {
            resetMulitplier();
        }

        return calcScore(counter, set.size());
    }

    public GamePiece spawnPiece() {
        GamePiece piece = GamePiece.createPiece(rand.nextInt(15));
        return piece;
    }

    public void nextPiece() {
        currentPiece = followingPiece;
        followingPiece = spawnPiece();
        nextPieceListener.nextPiece(currentPiece, followingPiece);

    }

    public SimpleIntegerProperty getLives() {
        return lives;
    }
    public void setLives(int newLivesNum){
        lives.set(newLivesNum);
    }

    public SimpleIntegerProperty getScore() {
        return score;
    }

    public SimpleIntegerProperty getLevel() {
        return level;
    }
    public void setLevel() {
        level.set(score.get()/1000);
    }
    public SimpleIntegerProperty getMultiplier() {
        return multiplier;
    }
    public void increaseMulitplier() {
        multiplier.set(multiplier.get() + 1);
    }
    public void resetMulitplier() {
        multiplier.setValue(1);
    }
    public void setScore(int num) {
        score.add(num);
    }

    public int calcScore(int numLines, int numBlocks) {
        return numLines * numBlocks * 10 * getMultiplier().get();
    }
    public void setNextPieceListener(NextPieceListener listener) {
        this.nextPieceListener = listener;
    }
    public void setLineClearedListener(LineClearedListener listener) {
        this.lineClearedListener = listener;
    }
    public void rotateCurrentPiece() {
        currentPiece.rotate();
        Multimedia.playSound("rotate.wav");
    }
    public void swapCurrentPiece() {
        GamePiece temp = currentPiece;
        currentPiece = followingPiece;
        followingPiece = temp;
        Multimedia.playSound("rotate.wav");
    }
    public GamePiece getCurrentPiece() {
        return currentPiece;
    }
    public GamePiece getFollowingPiece() {
        return followingPiece;
    }
    public int getTimerDelay() {
        return (int) Math.max(2.5, 12 - 0.500 * level.get());
    }
}
