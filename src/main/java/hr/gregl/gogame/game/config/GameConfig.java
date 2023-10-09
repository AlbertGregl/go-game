package hr.gregl.gogame.game.config;

public class GameConfig {

    private static final GameConfig instance = new GameConfig();

    private final int boardSize = 19; // for 19x19 board game

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
