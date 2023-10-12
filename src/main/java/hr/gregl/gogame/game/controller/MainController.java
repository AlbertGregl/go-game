package hr.gregl.gogame.game.controller;


import hr.gregl.gogame.game.MainApplication;
import hr.gregl.gogame.game.config.GameConfig;
import hr.gregl.gogame.game.model.GameLogic;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.util.Objects;


public class MainController {

    private final GameLogic gameLogic = new GameLogic();

    @FXML
    private GridPane boardGrid;
    @FXML
    private Label statusLabel;
    @FXML
    private Label blackCapturesLabel;
    @FXML
    private Label whiteCapturesLabel;
    @FXML
    private Label blackStonesLeftLabel;
    @FXML
    private Label whiteStonesLeftLabel;
    @FXML
    private Pane overlayPane;
    @FXML
    private Label winnerLabel;
    @FXML
    public Label playerTurnLabel;
    @FXML
    public Pane playerTurnPane;


    @FXML
    public void initialize() {
        playerTurnLabel.setText("Player 1 (Black) Turn");
        playerTurnPane.getStyleClass().add("player1Turn");
        overlayPane.setVisible(false);
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

        int gameRow = row - 1;
        int gameCol = col - 1;

        int currentPlayerBeforeMove = gameLogic.getCurrentPlayer();

        if (gameLogic.isValidMove(gameRow, gameCol, currentPlayerBeforeMove)) {
            System.out.println("Invalid move by " + currentPlayerBeforeMove + " at (" + gameRow + "," + gameCol + ")"); // debug
            return;
        }

        gameLogic.placeStone(gameRow, gameCol, currentPlayerBeforeMove);

        updateCurrentPlayerLbl(currentPlayerBeforeMove, clickedPane);

        Circle stone = new Circle(clickedPane.getWidth() / 2, clickedPane.getHeight() / 2, clickedPane.getWidth() / 2 - 5);
        if (currentPlayerBeforeMove == 1) {
            stone.setFill(Color.BLACK);
        } else if (currentPlayerBeforeMove == 2) {
            stone.setFill(Color.WHITE);
        }
        clickedPane.getChildren().add(stone);

        updateCaptureLabels();

        refreshBoard();

        updateStonesLeftLabels();

        gameOverCheck();
    }

    private void updateCurrentPlayerLbl(int currentPlayerBeforeMove, Pane clickedPane) {
        String player = (currentPlayerBeforeMove == 1) ? "Player 1 (Black)" : "Player 2 (White)";
        playerTurnLabel.setText((currentPlayerBeforeMove == 1) ? "Player 2 (White) Turn" : "Player 1 (Black) Turn");
        if (currentPlayerBeforeMove == 1) {
            playerTurnPane.getStyleClass().removeAll("player1Turn");
            playerTurnPane.getStyleClass().add("player2Turn");
        } else {
            playerTurnPane.getStyleClass().removeAll("player2Turn");
            playerTurnPane.getStyleClass().add("player1Turn");
        }
        statusLabel.setText(player + " moved to " + clickedPane.getId());
    }

    @FXML
    public void handleRestart() {
        gameLogic.reset();

        createGameBoard();

        statusLabel.setText("Game started.");
        blackCapturesLabel.setText("Captures: 0");
        whiteCapturesLabel.setText("Captures: 0");
        winnerLabel.setText("");

        overlayPane.setVisible(false);
    }

    @FXML
    public void handleSurrender() {
        DisplayScore(gameLogic.calculateScore(1), gameLogic.calculateScore(2));
        overlayPane.setVisible(true);
    }

    private void gameOverCheck() {
        if (gameLogic.isGameOver()) {
            int player1Score = gameLogic.calculateScore(1);
            int player2Score = gameLogic.calculateScore(2);

            DisplayScore(player1Score, player2Score);
            overlayPane.setVisible(true);
        }
    }

    private void DisplayScore(int player1Score, int player2Score) {
        if (player1Score > player2Score) {
            statusLabel.setText("Player 1 (Black) wins with " + player1Score + " points!");
            winnerLabel.setText("Player 1 (Black) wins!");
        } else if (player2Score > player1Score) {
            statusLabel.setText("Player 2 (White) wins with " + player2Score + " points!");
            winnerLabel.setText("Player 2 (White) wins!");
        } else {
            statusLabel.setText("It's a tie!");
            winnerLabel.setText("It's a tie!");
        }
    }

    private void createGameBoard() {
        boardGrid.getChildren().clear();
        setBoardBorderLabels();

        Image cellImage = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/boardCellInner.png")));
        Image cellImageStar = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/boardCellStarPoint.png")));
        Image topSideImage = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/boardCellTopSide.png")));
        Image bottomSideImage = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/boardCellBottomSide.png")));
        Image leftSideImage = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/boardCellLeftSide.png")));
        Image rightSideImage = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/boardCellRightSide.png")));
        Image cornerTopLeftImage = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/boardCellCornerTopLeft.png")));
        Image cornerTopRightImage = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/boardCellCornerTopRight.png")));
        Image cornerBottomLeftImage = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/boardCellCornerBottomLeft.png")));
        Image cornerBottomRightImage = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/boardCellCornerBottomRight.png")));

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
                pane.setId(GameConfig.getPaneId(i, j));

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
                } else if (GameConfig.isStarPoint(i, j)) {
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

    private void updateCaptureLabels() {
        blackCapturesLabel.setText("Captures: " + gameLogic.getBlackCaptures());
        whiteCapturesLabel.setText("Captures: " + gameLogic.getWhiteCaptures());
    }

    private void refreshBoard() {
        for (int i = 0; i < GameConfig.getInstance().getBoardSize(); i++) {
            for (int j = 0; j < GameConfig.getInstance().getBoardSize(); j++) {
                Pane cell = getPaneFromGrid(boardGrid, i + 1, j + 1);
                int cellState = gameLogic.getCellValue(i, j);
                assert cell != null;
                if (cellState == 0) {
                    cell.getChildren().clear();
                } else {
                    Circle stone = new Circle(cell.getWidth() / 2, cell.getHeight() / 2, cell.getWidth() / 2 - 5);
                    stone.setFill(cellState == 1 ? Color.BLACK : Color.WHITE);
                    cell.getChildren().setAll(stone);
                }
            }
        }
    }

    private void setBoardBorderLabels() {
        for (int i = 1; i <= GameConfig.getInstance().getBoardSize(); i++) {
            // Setting Column Labels
            Label columnLabel = new Label(String.valueOf(GameConfig.getColumnLabel(i - 1)));
            configureLabel(columnLabel);
            boardGrid.add(columnLabel, i, 0);

            // Setting Row Labels
            Label rowLabel = new Label(String.valueOf(GameConfig.getRowLabel(i - 1)));
            configureLabel(rowLabel);
            boardGrid.add(rowLabel, 0, i);
        }
    }

    private void configureLabel(Label label) {
        label.setAlignment(Pos.CENTER);
        label.setFont(new Font("MingLiU-ExtB", 24));
    }

    private Pane getPaneFromGrid(GridPane grid, int row, int col) {
        for (Node node : grid.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer colIndex = GridPane.getColumnIndex(node);
            if (node instanceof Pane && rowIndex != null && colIndex != null && rowIndex == row && colIndex == col) {
                return (Pane) node;
            }
        }
        return null;
    }

    private void updateStonesLeftLabels() {
        blackStonesLeftLabel.setText("Black Stones Left: " + GameConfig.getInstance().getBlackStonesLeft());
        whiteStonesLeftLabel.setText("White Stones Left: " + GameConfig.getInstance().getWhiteStonesLeft());
    }

}