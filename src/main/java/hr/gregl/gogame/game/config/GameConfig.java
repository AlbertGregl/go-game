package hr.gregl.gogame.game.config;

public class GameConfig {

    private static final GameConfig instance = new GameConfig();
    private static final char[] BOARD_LABELS = "ABCDEFGHJKLMNOPQRST".toCharArray();

    private static int boardSize = 19;
    private static int INITIAL_BLACK_STONES = 181;
    private static int INITIAL_WHITE_STONES = 180;

    private int blackStonesLeft = INITIAL_BLACK_STONES;
    private int whiteStonesLeft = INITIAL_WHITE_STONES;

    private GameConfig() {
    }

    public static void setBoardSize(int size) {
        boardSize = size;
        INITIAL_BLACK_STONES = (size * size) / 2 + 1;
        INITIAL_WHITE_STONES = (size * size) / 2;
    }

    public static GameConfig getInstance() {
        return instance;
    }

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

    public static boolean isStarPoint(int i, int j) {
        int middle = boardSize / 2 + 1;
        int firstStar = 4;
        int lastStar = boardSize - 3;

        return (i == firstStar || i == middle || i == lastStar) &&
                (j == firstStar || j == middle || j == lastStar);
    }

    public static String getPaneId(int i, int j) {
        if (j - 1 < BOARD_LABELS.length) {
            return BOARD_LABELS[j - 1] + Integer.toString(boardSize + 1 - i);
        } else {
            return "";
        }
    }

    public static char getColumnLabel(int index) {
        if (index >= 0 && index < boardSize) {
            return BOARD_LABELS[index];
        } else {
            throw new IndexOutOfBoundsException("Invalid column index");
        }
    }

    public static int getRowLabel(int index) {
        return boardSize - index;
    }

    public static void setBlackStonesLeft(int value) {
        instance.blackStonesLeft = value;
    }

    public static void setWhiteStonesLeft(int value) {
        instance.whiteStonesLeft = value;
    }

}

