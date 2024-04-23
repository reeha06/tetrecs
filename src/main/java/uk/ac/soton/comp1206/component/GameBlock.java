package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };

    private final GameBoard gameBoard;

    private final double width;
    private final double height;
    private static final double FADE_OUT_RATE = 0.015;
    private double opacity = 1.0;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    /**
     * Create a new single Game Block
     * @param gameBoard the board this block belongs to
     * @param x the column the block exists in
     * @param y the row the block exists in
     * @param width the width of the canvas to render
     * @param height the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);
    }

    /**
     * When the value of this block is updated,
     * @param observable what was updated
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     * paints empty if value of block is 0
     * or if the block belongs to the pieceboard and its the block in the middle of the pieceboard, then paint an indicator on it
     * otherwise paint the block with whatever its value is
     */
    public void paint() {
        //If the block is empty, paint as empty
        if(value.get() == 0) {
            paintEmpty();
        } else if (x == 1 && y == 1 && gameBoard instanceof PieceBoard) {
            paintCircle(COLOURS[value.get()]);
        }else {
            //If the block is not empty, paint with the colour represented by the value
            paintColor(COLOURS[value.get()]);
        }
    }

    /**
     * handles the painting of a block if the mouse hovers above it
     * adds a transparent white on top so it looks like its highlighted
     */
    public void hoverPaint() {
        if (!(gameBoard instanceof PieceBoard)) {
            var gc = getGraphicsContext2D();

            gc.setFill(Color.rgb(255, 255, 255, 0.5));
            gc.fillRect(0,0, width, height);

            gc.setStroke(Color.WHITE);
            gc.strokeRect(0,0,width,height);
        }
    }

    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Fill
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(0,0, width, height);

        //Border
        gc.setStroke(Color.WHITE);
        gc.strokeRect(0,0,width,height);
    }

    /**
     * Paint this canvas with the given colour
     * makes blocks look animated
     * @param colour the colour to paint
     */
    private void paintColor(Paint colour) {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Colour fill
        //gc.setFill(colour);

        //new 3D look
        Stop[] stops = new Stop[]{new Stop(0, Color.WHITE), new Stop(1, (Color) colour)};
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        gc.setFill(gradient);

        gc.fillRect(0,0, width, height);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);
    }

    /**
     * paints a circle on top of the block
     * @param colour the colour of the block to paint
     */
    private void paintCircle(Paint colour) {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);
        Stop[] stops = new Stop[]{new Stop(0, Color.WHITE), new Stop(1, (Color) colour)};
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        gc.setFill(gradient);

        //Colour fill
        gc.fillRect(0,0, width, height);

        gc.setFill(Color.rgb(255, 255, 255, 0.7));
        double cellWidth = 10;
        double cellHeight = 10;
        double circleRadius = Math.min(cellWidth, cellHeight) * 0.7; // Adjust the radius as needed
        double circleCenterX = getWidth() / 2.0;
        double circleCenterY = getHeight() / 2.0;

        gc.fillOval(circleCenterX - circleRadius, circleCenterY - circleRadius, circleRadius * 2, circleRadius * 2);
    }

    /**
     * Get the column of this block
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }

    /**
     * makes the block fade out from a green to nothing
     */
    public void fadeOut() {
        var gc = getGraphicsContext2D();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0,0,width,height);
                // Decrease opacity gradually
                opacity -= FADE_OUT_RATE;
                if (opacity <= 0) {
                    stop(); // Stop animation when opacity reaches 0
                    paintEmpty();
                }

                // Update block color with new opacity
                gc.setFill(Color.rgb(0, 255, 0, opacity));
                gc.fillRect(0,0, width, height);

                gc.setStroke(Color.WHITE);
                gc.strokeRect(0,0,width,height);
            }
        };
        timer.start();
    }

}
