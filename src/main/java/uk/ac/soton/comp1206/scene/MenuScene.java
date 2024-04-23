package uk.ac.soton.comp1206.scene;

import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.nio.file.Paths;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     * displays logo
     * displays two buttons
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        ImageView imageView = new ImageView(getClass().getResource("/images/TetrECS.png").toExternalForm());
        imageView.setFitWidth(487.5);
        imageView.setFitHeight(100);
        var button = new Button();
        button.setGraphic(imageView);
        button.setStyle("-fx-background-color: transparent;");

        VBox b = new VBox();
        var button1 = new Button("Single player");
        button1.getStyleClass().add("menuItem");
        button1.setOnMouseEntered(event -> button1.setStyle("-fx-text-fill: blue;")); // Change text color to red on hover
        button1.setOnMouseExited(event -> button1.setStyle("-fx-text-fill: black;"));

        var button2 = new Button("How to Play");
        button2.getStyleClass().add("menuItem");
        button2.setOnMouseEntered(event -> button2.setStyle("-fx-text-fill: blue;")); // Change text color to red on hover
        button2.setOnMouseExited(event -> button2.setStyle("-fx-text-fill: black;"));

        b.getChildren().addAll(button, button1, button2);
        b.setAlignment(Pos.CENTER);
        b.setSpacing(20);
        mainPane.setCenter(b);

        Multimedia.playAudio("menu.mp3");

        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(5), button);
        rotateTransition.setByAngle(360 * 2); // Rotate the button three times (360 degrees * 3)
        rotateTransition.setCycleCount(1); // Set the cycle count to 1 to spin the button only once
        rotateTransition.play();
        //Bind the button action to the startGame method in the menu
        button1.setOnAction(this::startGame);
        button2.setOnAction(this::instructions);
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {

    }

    /**
     * Handle when the Single player Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        gameWindow.startChallenge();
    }
    /**
     * Handle when the How to play Game button is pressed
     * @param event event
     */
    private void instructions(ActionEvent event) {
        gameWindow.loadScene(new InstructionsScene(gameWindow));
    }

}
