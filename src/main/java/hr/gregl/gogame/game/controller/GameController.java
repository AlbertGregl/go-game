package hr.gregl.gogame.game.controller;

import hr.gregl.gogame.game.config.GameConfig;
import hr.gregl.gogame.game.model.GameLogic;
import hr.gregl.gogame.game.model.UserType;
import hr.gregl.gogame.game.utility.*;
import hr.gregl.gogame.game.MainApplication;
import hr.gregl.gogame.game.networking.GameClient;
import hr.gregl.gogame.game.networking.GameServer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import hr.gregl.gogame.game.networking.GameStateUpdateListener;
import java.io.*;


public class GameController implements GameStateUpdateListener{

    private final GameLogic gameLogic = new GameLogic();
    @FXML
    public ToggleGroup gameBoardRBGroup;
    @FXML
    public Button loadButton;
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
    public Button surrenderBtn;
    @FXML
    public Pane mainMenuPane;
    @FXML
    public RadioButton board19x19RadioBtn;
    @FXML
    public RadioButton board13x13RadioBtn;
    @FXML
    public RadioButton board9x9RadioBtn;
    private GameServer gameServer;
    private GameClient gameClient;


    @FXML
    public void initialize() {
        initUserInterface();
    }

    private void initUserInterface() {
        playerTurnLabel.setText("Player 1 (Black) Turn");
        playerTurnPane.getStyleClass().add("player1Turn");
        overlayPane.setVisible(false);
        surrenderBtn.setVisible(true);
        mainMenuPane.setVisible(true);
    }

    @FXML
    private void handleCellClick(MouseEvent event) {
        Pane clickedPane = (Pane) event.getSource();

        Integer colInteger = GridPane.getColumnIndex(clickedPane);
        int col = (colInteger == null) ? 0 : colInteger;

        Integer rowInteger = GridPane.getRowIndex(clickedPane);
        int row = (rowInteger == null) ? 0 : rowInteger;

        int maxIndex = GameConfig.getInstance().getBoardSizeWithBorders() - 1;

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

        GameSaveState saveState = createGameSaveState();

        try {
            sendGameState(saveState);
        } catch (IOException e) {
            LogUtil.logError(e);
            throw new RuntimeException(e);
        }

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
        String paneId = clickedPane != null ? clickedPane.getId() : "N/A";
        statusLabel.setText(player + " moved to " + paneId);
    }

    @FXML
    public void handleRestart() {
        gameLogic.reset();

        createGameBoard();

        statusLabel.setText("Game started.");
        blackCapturesLabel.setText("Captures: 0");
        whiteCapturesLabel.setText("Captures: 0");
        winnerLabel.setText("");
        blackStonesLeftLabel.setText("Black Stones Left: " + GameConfig.getInstance().getBlackStonesLeft());
        whiteStonesLeftLabel.setText("White Stones Left: " + GameConfig.getInstance().getWhiteStonesLeft());

        overlayPane.setVisible(false);
        surrenderBtn.setVisible(true);
    }

    @FXML
    public void handleSurrender() {
        displayScore(gameLogic.calculateScore(1), gameLogic.calculateScore(2));
        overlayPane.setVisible(true);
        surrenderBtn.setVisible(false);
    }

    private void gameOverCheck() {
        if (gameLogic.isGameOver()) {
            int player1Score = gameLogic.calculateScore(1);
            int player2Score = gameLogic.calculateScore(2);

            displayScore(player1Score, player2Score);
            overlayPane.setVisible(true);
            surrenderBtn.setVisible(false);
        }
    }

    private void displayScore(int player1Score, int player2Score) {
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

        int boardSize = GameConfig.getInstance().getBoardSize();
        int maxIndex = GameConfig.getInstance().getBoardSizeWithBorders() - 1; // boardSize + 1

        for (int i = 1; i < maxIndex; i++) {
            for (int j = 1; j < maxIndex; j++) {
                Pane pane = getPane(i, j, boardSize);

                boardGrid.add(pane, j, i);
            }
        }
    }

    private Pane getPane(int i, int j, int boardSize) {
        Pane pane = new Pane();
        pane.setPrefSize(200, 200);
        pane.setId(GameConfig.getPaneId(i, j));

        if (i == 1 && j == 1) {
            pane.setBackground(BoardImageUtil.CORNER_TOP_LEFT.getBackground());
        } else if (i == 1 && j == boardSize) {
            pane.setBackground(BoardImageUtil.CORNER_TOP_RIGHT.getBackground());
        } else if (i == boardSize && j == 1) {
            pane.setBackground(BoardImageUtil.CORNER_BOTTOM_LEFT.getBackground());
        } else if (i == boardSize && j == boardSize) {
            pane.setBackground(BoardImageUtil.CORNER_BOTTOM_RIGHT.getBackground());
        } else if (i == 1) {
            pane.setBackground(BoardImageUtil.TOP_SIDE.getBackground());
        } else if (i == boardSize) {
            pane.setBackground(BoardImageUtil.BOTTOM_SIDE.getBackground());
        } else if (j == 1) {
            pane.setBackground(BoardImageUtil.LEFT_SIDE.getBackground());
        } else if (j == boardSize) {
            pane.setBackground(BoardImageUtil.RIGHT_SIDE.getBackground());
        } else if (GameConfig.isStarPoint(i, j)) {
            pane.setBackground(BoardImageUtil.STAR.getBackground());
        } else {
            pane.setBackground(BoardImageUtil.CELL.getBackground());
        }
        pane.setOnMouseClicked(this::handleCellClick);
        return pane;
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
            Label columnLabel = new Label(String.valueOf(GameConfig.getColumnLabel(i - 1)));
            configureLabel(columnLabel);
            boardGrid.add(columnLabel, i, 0);

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

    public void handlePlayGame() {
        if (board19x19RadioBtn.isSelected()) {
            GameConfig.setBoardSize(19);
        } else if (board13x13RadioBtn.isSelected()) {
            GameConfig.setBoardSize(13);
        } else if (board9x9RadioBtn.isSelected()) {
            GameConfig.setBoardSize(9);
        }
        handleRestart();
        mainMenuPane.setVisible(false);
    }

    public void handleSaveGameAction() {
        FileChooser fileChooser = GameIOUtil.configureFileChooser(
                "Save Game State",
                "Game save file",
                "*.gogame");
        File saveFile = fileChooser.showSaveDialog(boardGrid.getScene().getWindow());

        if (saveFile != null) {
            try {
                GameSaveState saveState = createGameSaveState();

                GameIOUtil.saveGame(saveFile, saveState);

            } catch (IOException e) {
                LogUtil.logError(e);
            }
        }
    }

    private GameSaveState createGameSaveState() {
        int[][] boardState = gameLogic.getBoard();
        return new GameSaveState(
                boardState,
                gameLogic.getBlackCaptures(),
                gameLogic.getWhiteCaptures(),
                GameConfig.getInstance().getBoardSize(),
                GameConfig.getInstance().getBlackStonesLeft(),
                GameConfig.getInstance().getWhiteStonesLeft(),
                gameLogic.getCurrentPlayer());
    }

    public void handleLoadGameAction() {
        FileChooser fileChooser = GameIOUtil.configureFileChooser(
                "Load Game State",
                "Game save file",
                "*.gogame");
        File loadFile = fileChooser.showOpenDialog(boardGrid.getScene().getWindow());

        if (loadFile != null) {
            try {
                GameSaveState loadedState = GameIOUtil.loadGame(loadFile);

                gameLogic.setBoard(loadedState.getBoardState());
                GameConfig.setBoardSize(loadedState.getBoardSize());
                gameLogic.setBlackCaptures(loadedState.getBlackCaptures());
                gameLogic.setWhiteCaptures(loadedState.getWhiteCaptures());
                GameConfig.setBlackStonesLeft(loadedState.getBlackStonesLeft());
                GameConfig.setWhiteStonesLeft(loadedState.getWhiteStonesLeft());

                updateCaptureLabels();
                refreshBoard();
                updateStonesLeftLabels();

            } catch (IOException | ClassNotFoundException e) {
                LogUtil.logError(e);
            }
        }
    }
    public void generateDocumentation() {
        FileChooser fileChooser = GameIOUtil.configureFileChooser(
                "Choose directory to save documentation",
                "HTML file",
                "*.html");
        File saveFile = fileChooser.showSaveDialog(boardGrid.getScene().getWindow());
        PrintDocumentationUtil.getInstance().printDocumentation(saveFile);
    }

    public void sendGameState(GameSaveState gameState) throws IOException {
        if (MainApplication.getUserType() == UserType.SERVER && gameServer != null) {
            try {
                gameServer.sendGameStateToClient(gameState);
            } catch (IOException e) {
                LogUtil.logError(e);
            }
        } else if (MainApplication.getUserType() == UserType.CLIENT && gameClient != null) {
            gameClient.sendGameState(gameState);
        }
    }


    @Override
    public void onGameStateReceived(GameSaveState gameState) {
        Platform.runLater(() -> {
            gameLogic.setBoard(gameState.getBoardState());
            gameLogic.setBlackCaptures(gameState.getBlackCaptures());
            gameLogic.setWhiteCaptures(gameState.getWhiteCaptures());

            GameConfig.setBlackStonesLeft(gameState.getBlackStonesLeft());
            GameConfig.setWhiteStonesLeft(gameState.getWhiteStonesLeft());

            refreshBoard();
            updateCaptureLabels();
            updateStonesLeftLabels();

            updateCurrentPlayerTurn(gameState);
        });
    }

    private void updateCurrentPlayerTurn(GameSaveState gameState) {
        int currentPlayer = gameState.getCurrentPlayer();
        updateCurrentPlayerLbl(currentPlayer, null);
    }

    public void setGameServer(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public void setGameClient(GameClient gameClient) {
        this.gameClient = gameClient;
    }
}