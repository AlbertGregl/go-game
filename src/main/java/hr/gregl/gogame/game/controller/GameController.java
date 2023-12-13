package hr.gregl.gogame.game.controller;

//region Imports
import hr.gregl.gogame.game.config.GameConfig;
import hr.gregl.gogame.game.model.GameLogic;
import hr.gregl.gogame.game.model.UserType;
import hr.gregl.gogame.game.utility.*;
import hr.gregl.gogame.game.MainApplication;
import hr.gregl.gogame.game.networking.GameClient;
import hr.gregl.gogame.game.networking.GameServer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import hr.gregl.gogame.game.networking.GameStateUpdateListener;
import hr.gregl.gogame.game.model.GameMove;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
//endregion

public class GameController implements GameStateUpdateListener, CellClickHandler {

    //region Game Logic Instance
    private final GameLogic gameLogic = new GameLogic();
    //endregion
    //region Networking Instances
    private GameServer gameServer;
    private GameClient gameClient;
    //endregion
    //region FXML Injected Fields
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
    @FXML
    public Button btnMsgSend;
    @FXML
    public TextField textFieldMsgTransmit;
    @FXML
    public TextArea textAreaMsgReceive;
    @FXML
    public GridPane gPaneMessages;
    @FXML
    public ScrollPane scrollPaneMsgReceived;
    //endregion
    // region Game Replay
    private final List<GameMove> gameMoves = new ArrayList<>();
    //endregion

    private boolean isGameOver = false;

    @FXML
    public void initialize() {
        initUserInterface();
    }

    @FXML
    @Override
    public void handleCellClick(MouseEvent event) {
        LogUtil.logDebug(LogUtil.debugLogMsg1 + "[" + MainApplication.getUserType() + "]");

        if ((MainApplication.getUserType() == UserType.SERVER && gameLogic.getCurrentPlayer() != 1) ||
                (MainApplication.getUserType() == UserType.CLIENT && gameLogic.getCurrentPlayer() != 2)) {
            statusLabel.setText("Not your turn!");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(MainApplication.getUserType().toString());
            alert.setHeaderText(null);
            alert.setContentText("It's not your turn!");
            return;
        }

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
            LogUtil.logDebug(LogUtil.debugLogMsg4 + currentPlayerBeforeMove + " at (" + gameRow + "," + gameCol + ")");
            return;
        }

        gameLogic.placeStone(gameRow, gameCol, currentPlayerBeforeMove);
        LogUtil.logInfo(LogUtil.infoLogMsg9 + gameRow + ", " + gameCol + " by player " + currentPlayerBeforeMove);

        if (MainApplication.getUserType() == UserType.SINGLE_PLAYER) {
            updateCurrentPlayerLbl(clickedPane);
            // game replay
            GameReplayUtil.getInstance().ensureReplayFileExists();
            GameMove move = new GameMove(currentPlayerBeforeMove, gameRow, gameCol);
            gameMoves.add(move);
            GameReplayUtil.getInstance().saveGameReplay(gameMoves);
        }

        Circle stone = new Circle(clickedPane.getWidth() / 2, clickedPane.getHeight() / 2, clickedPane.getWidth() / 2 - 5);
        if (currentPlayerBeforeMove == 1) {
            stone.setFill(Color.BLACK);
        } else if (currentPlayerBeforeMove == 2) {
            stone.setFill(Color.WHITE);
        }
        clickedPane.getChildren().add(stone);

        gameLogic.switchCurrentPlayer();

        updateCaptureLabels();
        refreshBoard();
        updateStonesLeftLabels();
        isGameOver = gameOverCheck();

        if (MainApplication.getUserType() != UserType.SINGLE_PLAYER) {
            GameSaveState saveState = createGameSaveState();
            try {
                LogUtil.logDebug(LogUtil.debugLogMsg2 + gameLogic.getCurrentPlayer());
                sendGameState(saveState);
            } catch (IOException e) {
                LogUtil.logError(e);
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void handleRestart() {
        gameLogic.reset();

        GameBoardUI.createGameBoard(boardGrid, this);

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

        isGameOver = true;
        if (MainApplication.getUserType() != UserType.SINGLE_PLAYER) {
            GameSaveState saveState = createGameSaveState();
            try {
                sendGameState(saveState);
            } catch (IOException e) {
                LogUtil.logError(e);
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void generateDocumentation() {
        FileChooser fileChooser = GameIOUtil.configureFileChooser(
                "Choose directory to save documentation",
                "HTML file",
                "*.html");
        File saveFile = fileChooser.showSaveDialog(boardGrid.getScene().getWindow());
        PrintDocumentationUtil.getInstance().printDocumentation(saveFile);
    }

    private void initUserInterface() {
        if (MainApplication.getUserType() == UserType.SERVER || MainApplication.getUserType() == UserType.SINGLE_PLAYER) {
            playerTurnLabel.setText("Player 1 (Black) Turn");
            playerTurnPane.getStyleClass().add("player1Turn");
        } else {
            playerTurnLabel.setText("Player 2 (White) Turn");
            playerTurnPane.getStyleClass().add("player2Turn");
        }
        overlayPane.setVisible(false);
        surrenderBtn.setVisible(true);
        mainMenuPane.setVisible(true);

        if (MainApplication.getUserType() == UserType.SINGLE_PLAYER) {
            gPaneMessages.setVisible(false);
        } else {
            textAreaMsgReceive.setEditable(false);
        }
    }

    private void updateCurrentPlayerLbl(Pane clickedPane) {
        int currentPlayer = gameLogic.getCurrentPlayer();
        String player = (currentPlayer == 1) ? "Player 1 (Black)" : "Player 2 (White)";
        playerTurnLabel.setText((currentPlayer == 1) ? "Player 2 (White) Turn" : "Player 1 (Black) Turn");
        if (currentPlayer == 1) {
            playerTurnPane.getStyleClass().removeAll("player1Turn");
            playerTurnPane.getStyleClass().add("player2Turn");
        } else {
            playerTurnPane.getStyleClass().removeAll("player2Turn");
            playerTurnPane.getStyleClass().add("player1Turn");
        }
        String paneId = clickedPane != null ? clickedPane.getId() : "N/A";
        statusLabel.setText(player + " moved to " + paneId);
    }

    private boolean gameOverCheck() {
        if (gameLogic.isGameOver()) {
            int player1Score = gameLogic.calculateScore(1);
            int player2Score = gameLogic.calculateScore(2);

            displayScore(player1Score, player2Score);
            overlayPane.setVisible(true);
            surrenderBtn.setVisible(false);

            return true;
        }
        return false;
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
        int[][] currentBoardState = gameLogic.getGameBoard().getBoardCopy();
        return new GameSaveState(
                currentBoardState,
                gameLogic.getBlackCaptures(),
                gameLogic.getWhiteCaptures(),
                GameConfig.getInstance().getBoardSize(),
                GameConfig.getInstance().getBlackStonesLeft(),
                GameConfig.getInstance().getWhiteStonesLeft(),
                gameLogic.getCurrentPlayer(),
                isGameOver
        );
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

                gameStateLoadReceive(loadedState);

            } catch (IOException | ClassNotFoundException e) {
                LogUtil.logError(e);
            }
        }
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
        LogUtil.logInfo(LogUtil.infoLogMsg8 + gameState.toString());
        gameLogic.setCurrentPlayer(gameState.getCurrentPlayer());
        Platform.runLater(() -> gameStateLoadReceive(gameState));

        if (gameState.isGameOver()) {
            Platform.runLater(() -> {
                displayScore(gameLogic.calculateScore(1), gameLogic.calculateScore(2));
                overlayPane.setVisible(true);
                surrenderBtn.setVisible(false);
            });
        }
    }

    private void gameStateLoadReceive(GameSaveState gameState) {
        LogUtil.logDebug(LogUtil.debugLogMsg3);
        gameLogic.setBoard(gameState.getBoardState());
        GameConfig.setBoardSize(gameState.getBoardSize());
        gameLogic.setBlackCaptures(gameState.getBlackCaptures());
        gameLogic.setWhiteCaptures(gameState.getWhiteCaptures());
        GameConfig.setBlackStonesLeft(gameState.getBlackStonesLeft());
        GameConfig.setWhiteStonesLeft(gameState.getWhiteStonesLeft());
        isGameOver = gameState.isGameOver();

        updateCaptureLabels();
        refreshBoard();
        updateStonesLeftLabels();
        LogUtil.logDebug(LogUtil.debugLogMsg9 + gameLogic.getCurrentPlayer());
    }

    public void setGameServer(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public void setGameClient(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    public void handleMessageSend() throws IOException {
        String message = textFieldMsgTransmit.getText();
        if (message != null && !message.isEmpty()) {
            LogUtil.logInfo("TX: " + MainApplication.getUserType().toString() + ": " + message);
            MessageState messageState = new MessageState(message, MainApplication.getUserType().toString());
            Platform.runLater(() -> textAreaMsgReceive.appendText(MainApplication.getUserType().toString() + ": " + message + "\n"));
            if (MainApplication.getUserType() == UserType.SERVER && gameServer != null) {
                gameServer.sendMessageToClient(messageState);
            } else if (MainApplication.getUserType() == UserType.CLIENT && gameClient != null) {
                gameClient.sendMessage(messageState);
            }
            textFieldMsgTransmit.clear();
        }
    }

    @Override
    public void onMessageReceived(MessageState messageState) {
        LogUtil.logInfo("RX: " + messageState.getSender() + ": " + messageState.getMessage());
        Platform.runLater(() -> textAreaMsgReceive.appendText(messageState.getSender() + ": " + messageState.getMessage() + "\n"));
    }

    @FXML
    public void handleReplay() {
        List<GameMove> replayMoves = GameReplayUtil.getInstance().loadGameReplay();
        if (replayMoves.isEmpty()) {
            showReplayAlert();
            return;
        }

        handleRestart();
        AtomicInteger moveIndex = new AtomicInteger(0);

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            if (moveIndex.get() < replayMoves.size()) {
                GameMove move = replayMoves.get(moveIndex.getAndIncrement());
                replayMove(move);
                pause.playFromStart();
            }
        });

        pause.play();
    }

    private void replayMove(GameMove move) {
        int currentPlayerBeforeMove = move.getPlayer();
        gameLogic.placeStone(move.getRow(), move.getColumn(), currentPlayerBeforeMove);

        Circle stone = new Circle(boardGrid.getWidth() / 2, boardGrid.getHeight() / 2, boardGrid.getWidth() / 2 - 5);
        stone.setFill(currentPlayerBeforeMove == 1 ? Color.BLACK : Color.WHITE);

        Pane cell = getPaneFromGrid(boardGrid, move.getRow() + 1, move.getColumn() + 1);
        if (cell != null) {
            cell.getChildren().add(stone);
        }

        gameLogic.switchCurrentPlayer();
        updateCaptureLabels();
        refreshBoard();
        updateStonesLeftLabels();
        isGameOver = gameOverCheck();
    }

    private void showReplayAlert() {
        winnerLabel.setText("No replay moves found!");
    }

}