package hr.gregl.gogame.game.config;

public class GameConfig {

    private static final GameConfig instance = new GameConfig();

    private final int boardSize = 19; // for 19x19 board game

    private int blackStonesLeft = 181;
    private int whiteStonesLeft = 180;

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
}
