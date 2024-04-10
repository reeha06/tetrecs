package uk.ac.soton.comp1206.scene;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;
    PieceBoard pieceBoard;
    PieceBoard pieceBoard2;
    BorderPane mainPane;
    VBox b = new VBox();
    GameBoard board;

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

        Multimedia.stopAudio();
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

        Label multiplierLabel = new Label();
        multiplierLabel.textProperty().bind(game.getMultiplier().asString("Multiplier: x%d"));
        multiplierLabel.setFont(new Font(42));
        multiplierLabel.setStyle("-fx-text-fill: white;");

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
                //Left-click detected
                game.rotateCurrentPiece();
                GamePiece piece = game.getCurrentPiece();
                pieceBoard.displayPiece(piece);
            }
        });

        pieceBoard2 = new PieceBoard(new Grid(3, 3), 100, 100);
        pieceBoard2.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                //Left-click detected
                game.swapCurrentPiece();
                GamePiece piece = game.getCurrentPiece();
                pieceBoard.displayPiece(piece);
                GamePiece piece2 = game.getFollowingPiece();
                pieceBoard2.displayPiece(piece2);
            }
        });

//        board.setOnMouseClicked(event -> {
//            if (event.getButton() == MouseButton.SECONDARY) {
//                //Right-click detected
//                game.rotateCurrentPiece();
//                GamePiece piece = game.getCurrentPiece();
//                pieceBoard.displayPiece(piece);
//            }
//        });

        Insets insets = new Insets(20, 20, 20, 20);
        pieceBoard2.setPadding(insets);

        b.getChildren().addAll(livesLabel, scoreLabel, levelLabel, multiplierLabel, incoming, pieceBoard, pieceBoard2);
        game.setNextPieceListener(this::showPiece);

        mainPane.setRight(b);
//         create timeline animation
        Timeline timeline = new Timeline();
//         create rectangle with initial width of 200
        Rectangle rectangle = new Rectangle(800, 50, Color.BLUE);

//         define the keyframe for animating rectangle's width to 0
        KeyFrame shrinkKeyFrame = new KeyFrame(Duration.seconds(5), new KeyValue(rectangle.widthProperty(), 0));

        timeline.getKeyFrames().add(shrinkKeyFrame);
        timeline.setOnFinished(e -> {
            // decrease lives by 1 when animation finishes
            game.setLives(game.getLives().get() - 1);

            // check if lives are above 0 before restarting animation
            if (game.getLives().get() > 0) {
                // reset rectangle's width
                rectangle.setWidth(800);
                timeline.playFromStart();
            }
        });
        // start animation
        timeline.play();
        mainPane.setBottom(rectangle);

    }
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
        //game.playPiece(gameBlock);
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
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();
    }

}
