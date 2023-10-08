package hr.gregl.gogame.game;


import hr.gregl.gogame.game.model.GameLogic;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.io.InputStream;
import java.util.Objects;


public class MainController {

    private final GameLogic gameLogic = new GameLogic(19); // 19x19 board game

    @FXML
    private GridPane boardGrid;
    @FXML
    private VBox controlPanel;
    @FXML
    private HBox statusPanel;

    @FXML
    public void initialize() {
        createGameBoard(20, 20);
    }

    @FXML
    private void handleCellClick(MouseEvent event) {
        Pane clickedPane = (Pane) event.getSource();

        Integer colInteger = GridPane.getColumnIndex(clickedPane);
        int col = (colInteger == null) ? 0 : colInteger.intValue();

        Integer rowInteger = GridPane.getRowIndex(clickedPane);
        int row = (rowInteger == null) ? 0 : rowInteger.intValue();

        System.out.println("Handling click at row: " + row + ", col: " + col); // debug

        int currentPlayer = gameLogic.getCurrentPlayer();
        gameLogic.placeStone(row, col, currentPlayer);

        Circle stone = new Circle(clickedPane.getWidth() / 2, clickedPane.getHeight() / 2, clickedPane.getWidth() / 2 - 5);
        if (currentPlayer == 1) {
            stone.setFill(Color.BLACK);
        } else if (currentPlayer == 2) {
            stone.setFill(Color.WHITE);
        }
        clickedPane.getChildren().add(stone);
    }

    private void createGameBoard(int rows, int cols) {
        Image cellImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/boardCellInner.png")));
        Image cellImageStar = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/boardCellStarPoint.png")));
        Image cellImageBorder = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/boardCellBorder.png")));
        Image topSideImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/boardCellTopSide.png")));
        Image bottomSideImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/boardCellBottomSide.png")));
        Image leftSideImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/boardCellLeftSide.png")));
        Image rightSideImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/boardCellRightSide.png")));
        Image cornerTopLeftImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/boardCellCornerTopLeft.png")));
        Image cornerTopRightImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/boardCellCornerTopRight.png")));
        Image cornerBottomLeftImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/boardCellCornerBottomLeft.png")));
        Image cornerBottomRightImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/boardCellCornerBottomRight.png")));

        Background background = createBackgroundFromImage(cellImage);
        Background starBackground = createBackgroundFromImage(cellImageStar);
        Background borderBackground = createBackgroundFromImage(cellImageBorder);
        Background topSideBackground = createBackgroundFromImage(topSideImage);
        Background bottomSideBackground = createBackgroundFromImage(bottomSideImage);
        Background leftSideBackground = createBackgroundFromImage(leftSideImage);
        Background rightSideBackground = createBackgroundFromImage(rightSideImage);
        Background cornerTopLeftBackground = createBackgroundFromImage(cornerTopLeftImage);
        Background cornerTopRightBackground = createBackgroundFromImage(cornerTopRightImage);
        Background cornerBottomLeftBackground = createBackgroundFromImage(cornerBottomLeftImage);
        Background cornerBottomRightBackground = createBackgroundFromImage(cornerBottomRightImage);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Pane pane = new Pane();
                pane.setPrefSize(200, 200);

                if (i == 0 || i == 19 || j == 0 || j == 19) {
                    pane.setBackground(borderBackground);
                } else {
                    if (i == 1 && j == 1) {
                        pane.setBackground(cornerTopLeftBackground);
                    } else if (i == 1 && j == 18) {
                        pane.setBackground(cornerTopRightBackground);
                    } else if (i == 18 && j == 1) {
                        pane.setBackground(cornerBottomLeftBackground);
                    } else if (i == 18 && j == 18) {
                        pane.setBackground(cornerBottomRightBackground);
                    } else if (i == 1) {
                        pane.setBackground(topSideBackground);
                    } else if (i == 18) {
                        pane.setBackground(bottomSideBackground);
                    } else if (j == 1) {
                        pane.setBackground(leftSideBackground);
                    } else if (j == 18) {
                        pane.setBackground(rightSideBackground);
                    } else if (isStarPoint(i, j)) {
                        pane.setBackground(starBackground);
                    } else {
                        pane.setBackground(background);
                    }
                    pane.setOnMouseClicked(this::handleCellClick);
                }

                boardGrid.add(pane, j, i);
            }
        }
    }

    private Background createBackgroundFromImage(Image image) {
        BackgroundSize size = new BackgroundSize(200, 200, false, false, false, false);
        return new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size));
    }

    private boolean isCorner(int i, int j, int rows, int cols) {
        return (i == 1 || i == rows - 2) && (j == 1 || j == cols - 2);
    }

    private boolean isSide(int i, int j, int rows, int cols) {
        return i == 1 || i == rows - 2 || j == 1 || j == cols - 2;
    }

    private boolean isStarPoint(int i, int j) {
        return (i == 3 || i == 9 || i == 15) && (j == 3 || j == 9 || j == 15);
    }



}