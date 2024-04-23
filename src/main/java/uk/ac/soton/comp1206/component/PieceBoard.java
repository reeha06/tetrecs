package uk.ac.soton.comp1206.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.scene.MenuScene;

public class PieceBoard extends GameBoard{
    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    /**
     * Create a new PieceBoard, based off a given grid, with a visual width and height.
     * @param grid linked grid
     * @param width the visual width
     * @param height the visual height
     */
    public PieceBoard(Grid grid, double width, double height) {
        super(grid, width, height);
    }

    /**
     * @param currentPiece the piece to display on the pieceboard
     */
    public void displayPiece(GamePiece currentPiece) {
        logger.info("Piece displayed: " + currentPiece.toString());
        int[][] placePiece = currentPiece.getBlocks();

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                grid.set(x, y, placePiece[x][y]);
            }
        }
    }
}
