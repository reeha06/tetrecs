package uk.ac.soton.comp1206.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.scene.MenuScene;

public class PieceBoard extends GameBoard{
    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    //protected final Grid grid;
    protected GamePiece piece = null;
    public PieceBoard(Grid grid, double width, double height) {
        super(grid, width, height);
        //this.grid = new Grid(3,3);
        //this.piece = currentPiece;
    }
    public void displayPiece(GamePiece currentPiece) {
        this.piece = currentPiece;
        int[][] placePiece = currentPiece.getBlocks();

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                grid.set(x, y, placePiece[x][y]);
                logger.info(x + ", " + y + ", " + placePiece[x][y]);
            }
        }
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                logger.info(grid.get(x, y));
            }
        }
        logger.info("Displayed piece: " + currentPiece.toString());
    }
}
