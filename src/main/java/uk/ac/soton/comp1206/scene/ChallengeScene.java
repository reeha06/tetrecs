package uk.ac.soton.comp1206.scene;

import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.Set;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    /**
     * game
     */
    protected Game game;
    /**
     * the pieceBoard that has the currentPiece
     */
    private PieceBoard pieceBoard;
    /**
     * the pieceBoard that has the followingPiece
     */
    private PieceBoard pieceBoard2;
    private BorderPane mainPane;
    private VBox b = new VBox();
    /**
     * the GameBoard where player places pieces
     */
    private GameBoard board;
    /**
     * used for the countdown animation
     * create rectangle with initial width of 200
     */
    private Rectangle rectangle = new Rectangle(800, 50, Color.GREEN);
    private GameBlock block;
    /**
     * to keep track of the x when positioning and dropping pieces via the keyboard
     */
    private int x = 0;
    /**
     * to keep track of the y when positioning and dropping pieces via the keyboard
     */
    private int y = 0;

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        // stops any other audio playing
        Multimedia.stopAudio();
        // plays game music
        Multimedia.playMusic("game_start.wav");

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);

        mainPane.setCenter(board);
        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);

        Label livesLabel = new Label("Lives left = 3");
        livesLabel.textProperty().bind(game.getLives().asString("Lives: %d"));
        livesLabel.setFont(new Font(42));
        livesLabel.setStyle("-fx-text-fill: white;");
        livesLabel.getStyleClass().add("lives");

        Label levelLabel = new Label();
        levelLabel.textProperty().bind(game.getLevel().asString("Level: %d"));
        levelLabel.setFont(new Font(42));
        levelLabel.setStyle("-fx-text-fill: white;");
        levelLabel.getStyleClass().add("level");

//        multiplier used for testing
//        Label multiplierLabel = new Label();
//        multiplierLabel.textProperty().bind(game.getMultiplier().asString("Multiplier: x%d"));
//        multiplierLabel.setFont(new Font(42));
//        multiplierLabel.setStyle("-fx-text-fill: white;");

        Label scoreLabel = new Label();
        scoreLabel.textProperty().bind(game.getScore().asString("Score: %d"));
        scoreLabel.setFont(new Font(42));
        scoreLabel.setStyle("-fx-text-fill: white;");
        scoreLabel.getStyleClass().add("score");

        Label incoming = new Label("Incoming");
        incoming.setFont(new Font(42));
        incoming.setStyle("-fx-text-fill: white;");
        incoming.getStyleClass().add("score");

        pieceBoard = new PieceBoard(new Grid(3, 3), 150, 150);
        pieceBoard.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                //Left-click detected so rotates piece
                game.rotateCurrentPiece();
                GamePiece piece = game.getCurrentPiece();
                pieceBoard.displayPiece(piece);
            }
        });

        pieceBoard2 = new PieceBoard(new Grid(3, 3), 100, 100);
        pieceBoard2.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                //Left-click detected so swaps pieces on both pieceBoards
                game.swapCurrentPiece();
                GamePiece piece = game.getCurrentPiece();
                pieceBoard.displayPiece(piece);
                GamePiece piece2 = game.getFollowingPiece();
                pieceBoard2.displayPiece(piece2);
            }
        });

        Insets insets = new Insets(20, 20, 20, 20);
        pieceBoard2.setPadding(insets);

        // labels displayed
        b.getChildren().addAll(livesLabel, scoreLabel, levelLabel, incoming, pieceBoard, pieceBoard2);
        game.setNextPieceListener(this::showPiece);
        game.setLineClearedListener(this::onLineCleared);

        mainPane.setRight(b);

//         create timeline animation
        Timeline timeline = new Timeline();

        KeyFrame shrinkKeyFrame = new KeyFrame(Duration.seconds(game.getTimerDelay()), new KeyValue(rectangle.widthProperty(), 0));
        KeyFrame newColour = new KeyFrame(Duration.seconds(game.getTimerDelay()), e -> changeTransition());

        timeline.getKeyFrames().add(shrinkKeyFrame);
        timeline.getKeyFrames().add(newColour);
        timeline.setOnFinished(e -> {
            // decrease lives by 1 when animation finishes
            game.setLives(game.getLives().get() - 1);
            game.nextPiece();
            game.resetMulitplier();

            // check if lives are above 0 before restarting animation
            if (game.getLives().get() > 0) {
                // reset rectangle's width
                rectangle.setWidth(800);
                timeline.playFromStart();
            }
            // if lives is 0 or less then game is finished
            if (game.getLives().get() <= 0) {
                Platform.runLater(this::gameFinished);
            }
        });
        // start animation
        timeline.play();
        mainPane.setBottom(rectangle);


    }

    /**
     * changes the animated countdown from green to red as urgency increases
     */
    public void changeTransition() {
        logger.info("New trans");
        FillTransition fillTransition = new FillTransition(Duration.seconds(game.getTimerDelay()), rectangle);
        fillTransition.setFromValue(Color.GREEN);
        fillTransition.setToValue(Color.RED);
        fillTransition.play();
    }

    /**
     * when game is over, the ScoresScene is shown
     */
    public void gameFinished() {
        logger.info("Game over.");
        gameWindow.loadScene(new ScoresScene(gameWindow, game));
    }

    /**
     * does a fade out animation on the lines that were cleared in the board
     * @param clearedBlocks
     */
    public void onLineCleared(Set<GameBlockCoordinate> clearedBlocks) {
        board.fadeOut(clearedBlocks);
    }

    /**
     * displays pieces on pieceBoards
     * @param piece displayed on pieceBoard
     * @param piece2 displayed on pieceBoard2
     */
    protected void showPiece(GamePiece piece, GamePiece piece2) {
        logger.info("Displaying piece: " + piece.toString());
        pieceBoard.displayPiece(piece);
        pieceBoard2.displayPiece(piece2);
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     * handles keyboard support
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();

        scene.setOnKeyPressed(event -> {
            block = board.getBlock(x, y);
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.X) {
                blockClicked(block);
            } else if (event.getCode() == KeyCode.ESCAPE) {
                gameWindow.startMenu();
            } else if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.D) {
                if (x + 1 >= 0 && x + 1 < 5) {
                    logger.info("X: " + x + " Y: " + x);
                    block.paint();
                    x++;
                    block = board.getBlock(x, y);
                    block.hoverPaint();
                }
            } else if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.A) {
                if (x - 1 >= 0 && x - 1 < 5) {
                    block.paint();
                    x--;
                    block = board.getBlock(x, y);
                    block.hoverPaint();
                }
            } else if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.W) {
                if (y - 1 >= 0 && y - 1 < 5) {
                    block.paint();
                    y--;
                    block = board.getBlock(x, y);
                    block.hoverPaint();
                }
            } else if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.S) {
                if (y + 1 >= 0 && y + 1 < 5) {
                    block.paint();
                    y++;
                    block = board.getBlock(x, y);
                    block.hoverPaint();
                }
            } else if (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.R) {
                game.swapCurrentPiece();
                GamePiece piece = game.getCurrentPiece();
                pieceBoard.displayPiece(piece);
                GamePiece piece2 = game.getFollowingPiece();
                pieceBoard2.displayPiece(piece2);
            } else if (event.getCode() == KeyCode.Q || event.getCode() == KeyCode.Z || event.getCode() == KeyCode.OPEN_BRACKET) {
                game.rotateCurrentPiece();
                game.rotateCurrentPiece();
                game.rotateCurrentPiece();
                GamePiece piece = game.getCurrentPiece();
                pieceBoard.displayPiece(piece);
            } else if (event.getCode() == KeyCode.E || event.getCode() == KeyCode.C || event.getCode() == KeyCode.CLOSE_BRACKET) {
                game.rotateCurrentPiece();
                GamePiece piece = game.getCurrentPiece();
                pieceBoard.displayPiece(piece);
            }
        });
    }

}
