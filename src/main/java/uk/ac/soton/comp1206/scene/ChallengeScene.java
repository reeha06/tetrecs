package uk.ac.soton.comp1206.scene;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;

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

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        var board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);
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

        VBox b = new VBox();
        Label scoreLabel = new Label();
        scoreLabel.textProperty().bind(game.getScore().asString("Score: %d"));
        scoreLabel.setFont(new Font(42));
        scoreLabel.setStyle("-fx-text-fill: white;");
        scoreLabel.getStyleClass().add("score");

        Label currentShape = new Label();
        // currentShape.textProperty().bind(game.currentPiece.toString());
        currentShape.setFont(new Font(42));
        currentShape.setStyle("-fx-text-fill: white;");
        b.getChildren().addAll(livesLabel, scoreLabel, levelLabel, multiplierLabel, currentShape);

        mainPane.setRight(b);
        // create timeline animation
        Timeline timeline = new Timeline();
        // create rectangle with initial width of 200
        Rectangle rectangle = new Rectangle(800, 50, Color.BLUE);

        // define the keyframe for animating rectangle's width to 0
        KeyFrame shrinkKeyFrame = new KeyFrame(Duration.seconds(5), new KeyValue(rectangle.widthProperty(), 0));

        timeline.getKeyFrames().add(shrinkKeyFrame);
        timeline.setOnFinished(e -> {
            // decrease lives by 1 when animation finishes
            game.lives.set(game.lives.get() - 1);

            // check if lives are above 0 before restarting animation
            if (game.lives.get() > 0) {
                // reset rectangle's width
                rectangle.setWidth(800);
                timeline.playFromStart();
            }
        });
        // start animation
        timeline.play();
        mainPane.setBottom(rectangle);

        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);
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
