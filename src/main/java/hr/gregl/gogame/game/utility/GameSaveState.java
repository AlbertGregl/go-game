package hr.gregl.gogame.game.utility;

import java.io.Serial;
import java.io.Serializable;

public class GameSaveState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int[][] boardState;
    private final int blackCaptures;
    private final int whiteCaptures;
    private final int boardSize;
    private final int blackStonesLeft;
    private final int whiteStonesLeft;
    private final int currentPlayer;

    public GameSaveState(int[][] boardState, int blackCaptures, int whiteCaptures, int boardSize, int blackStonesLeft, int whiteStonesLeft, int currentPlayer) {
        this.boardState = boardState;
        this.blackCaptures = blackCaptures;
        this.whiteCaptures = whiteCaptures;
        this.boardSize = boardSize;
        this.blackStonesLeft = blackStonesLeft;
        this.whiteStonesLeft = whiteStonesLeft;
        this.currentPlayer = currentPlayer;
    }

    public int[][] getBoardState() {
        return boardState;
    }

    public int getBlackCaptures() {
        return blackCaptures;
    }

    public int getWhiteCaptures() {
        return whiteCaptures;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getBlackStonesLeft() {
        return blackStonesLeft;
    }

    public int getWhiteStonesLeft() {
        return whiteStonesLeft;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }
}

