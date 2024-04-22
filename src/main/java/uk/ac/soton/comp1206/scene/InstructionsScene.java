package uk.ac.soton.comp1206.scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class InstructionsScene extends BaseScene{
    private static final Logger logger = LogManager.getLogger(InstructionsScene.class);
    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating instructions Scene");
    }

    @Override
    public void initialise() {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                gameWindow.startMenu();
            }
        });
    }

    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var instructionsPane = new StackPane();
        instructionsPane.setMaxWidth(gameWindow.getWidth());
        instructionsPane.setMaxHeight(gameWindow.getHeight());
        instructionsPane.getStyleClass().add("menu-background");

        root.getChildren().add(instructionsPane);

        var mainPane = new BorderPane();
        instructionsPane.getChildren().add(mainPane);

        //Image image = new Image("/images/Instructions.png");
        ImageView imageView = new ImageView(getClass().getResource("/images/Instructions.png").toExternalForm());
        imageView.setFitWidth(487.5);
        imageView.setFitHeight(300);
        var button = new Button();
        var title = new Text("Instructions");
        title.getStyleClass().add("title");
        button.setGraphic(imageView);
        //button.setDisable(true);
        button.setStyle("-fx-background-color: transparent;");
        VBox box = new VBox();
        box.getChildren().addAll(title, button);
        mainPane.setCenter(box);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        int counter = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                PieceBoard pieceBoard = new PieceBoard(new Grid(3, 3), 75, 75);
                GamePiece piece = GamePiece.createPiece(counter);
                counter++;
                pieceBoard.displayPiece(piece);
                gridPane.add(pieceBoard, col, row);
            }
        }

        mainPane.setBottom(gridPane);
    }
}
