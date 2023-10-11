package hr.gregl.gogame.game.config;

public class GameConfig {

    private static final GameConfig instance = new GameConfig();

    private final int boardSize = 19; // for 19x19 board game

    private static final int INITIAL_BLACK_STONES = 181;
    private static final int INITIAL_WHITE_STONES = 180;

    private int blackStonesLeft = INITIAL_BLACK_STONES;
    private int whiteStonesLeft = INITIAL_WHITE_STONES;

    public int getBlackStonesLeft() {
        return blackStonesLeft;
    }

    public void decreaseBlackStones() {
        if (blackStonesLeft > 0) {
            blackStonesLeft--;
        }
    }

    public int getWhiteStonesLeft() {
        return whiteStonesLeft;
    }

    public void decreaseWhiteStones() {
        if (whiteStonesLeft > 0) {
            whiteStonesLeft--;
        }
    }

    private GameConfig() {}

    public static GameConfig getInstance() {
        return instance;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getBoardSizeWithBorders() {
        return boardSize + 2;
    }

    public void resetStones() {
        blackStonesLeft = INITIAL_BLACK_STONES;
        whiteStonesLeft = INITIAL_WHITE_STONES;
    }
}
