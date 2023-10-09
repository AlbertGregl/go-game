package hr.gregl.gogame.game;


import hr.gregl.gogame.game.config.GameConfig;
import hr.gregl.gogame.game.model.GameLogic;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.Objects;


public class MainController {

    private final GameLogic gameLogic = new GameLogic(GameConfig.getInstance().getBoardSize());

    @FXML
    private GridPane boardGrid;

    @FXML
    private VBox controlPanel;
    @FXML
    private HBox statusPanel;

    @FXML
    public void initialize() {
        createGameBoard();
    }

    @FXML
    private void handleCellClick(MouseEvent event) {
        Pane clickedPane = (Pane) event.getSource();

        Integer colInteger = GridPane.getColumnIndex(clickedPane);
        int col = (colInteger == null) ? 0 : colInteger;

        Integer rowInteger = GridPane.getRowIndex(clickedPane);
        int row = (rowInteger == null) ? 0 : rowInteger;

        int maxIndex = GameConfig.getInstance().getBoardSizeWithBorders() - 1;

        // ignore clicks on the border of the board.
        if (row == 0 || row == maxIndex || col == 0 || col == maxIndex) {
            return;
        }

        System.out.println("Handling click at row: " + row + ", col: " + col); // debug

        int gameRow = row - 1;
        int gameCol = col - 1;

        int currentPlayer = gameLogic.getCurrentPlayer();
        gameLogic.placeStone(gameRow, gameCol, currentPlayer);

        Circle stone = new Circle(clickedPane.getWidth() / 2, clickedPane.getHeight() / 2, clickedPane.getWidth() / 2 - 5);
        if (currentPlayer == 1) {
            stone.setFill(Color.BLACK);
        } else if (currentPlayer == 2) {
            stone.setFill(Color.WHITE);
        }
        clickedPane.getChildren().add(stone);
    }

    private void createGameBoard() {
        Image cellImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/boardCellInner.png")));
        Image cellImageStar = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/boardCellStarPoint.png")));
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
        Background topSideBackground = createBackgroundFromImage(topSideImage);
        Background bottomSideBackground = createBackgroundFromImage(bottomSideImage);
        Background leftSideBackground = createBackgroundFromImage(leftSideImage);
        Background rightSideBackground = createBackgroundFromImage(rightSideImage);
        Background cornerTopLeftBackground = createBackgroundFromImage(cornerTopLeftImage);
        Background cornerTopRightBackground = createBackgroundFromImage(cornerTopRightImage);
        Background cornerBottomLeftBackground = createBackgroundFromImage(cornerBottomLeftImage);
        Background cornerBottomRightBackground = createBackgroundFromImage(cornerBottomRightImage);

        int boardSize = GameConfig.getInstance().getBoardSize();
        int maxIndex = GameConfig.getInstance().getBoardSizeWithBorders() - 1; // boardSize + 1

        for (int i = 1; i < maxIndex; i++) {
            for (int j = 1; j < maxIndex; j++) {
                Pane pane = new Pane();
                pane.setPrefSize(200, 200);

                if (i == 1 && j == 1) {
                    pane.setBackground(cornerTopLeftBackground);
                } else if (i == 1 && j == boardSize) {
                    pane.setBackground(cornerTopRightBackground);
                } else if (i == boardSize && j == 1) {
                    pane.setBackground(cornerBottomLeftBackground);
                } else if (i == boardSize && j == boardSize) {
                    pane.setBackground(cornerBottomRightBackground);
                } else if (i == 1) {
                    pane.setBackground(topSideBackground);
                } else if (i == boardSize) {
                    pane.setBackground(bottomSideBackground);
                } else if (j == 1) {
                    pane.setBackground(leftSideBackground);
                } else if (j == boardSize) {
                    pane.setBackground(rightSideBackground);
                } else if (isStarPoint(i, j)) {
                    pane.setBackground(starBackground);
                } else {
                    pane.setBackground(background);
                }
                pane.setOnMouseClicked(this::handleCellClick);

                boardGrid.add(pane, j, i);
            }
        }
    }

    private Background createBackgroundFromImage(Image image) {
        BackgroundSize size = new BackgroundSize(200, 200, false, false, false, false);
        return new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size));
    }

    private boolean isStarPoint(int i, int j) {
        return (i == 4 || i == 10 || i == 16) && (j == 4 || j == 10 || j == 16);
    }

}