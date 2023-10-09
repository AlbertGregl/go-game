package hr.gregl.gogame.game.model;

import hr.gregl.gogame.game.config.GameConfig;

public class GameLogic {

    private final GameBoard gameBoard;
    private int currentPlayer = 1; // 1 - black; 2 - white
    private int[][] previousBoardState;
    private final int BOARD_SIZE;

    public GameLogic() {
        this.BOARD_SIZE = GameConfig.getInstance().getBoardSize();
        this.gameBoard = new GameBoard(BOARD_SIZE);
        this.previousBoardState = new int[BOARD_SIZE][BOARD_SIZE];
        initializePreviousBoardState();
    }

    private void initializePreviousBoardState() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                previousBoardState[i][j] = gameBoard.getCell(i, j);
            }
        }
    }

    public int getCurrentPlayer() {

        return currentPlayer;
    }


    public boolean isValidMove(int row, int col, int player) {
        System.out.println("Checking if move is valid at (" + row + "," + col + ") for player " + player);

        if (gameBoard.hasStone(row, col)) {
            System.out.println("Cell already occupied.");
            return true;
        }


        int[][] tempBoard = gameBoard.getBoardCopy();
        tempBoard[row][col] = player;

        return isSuicidalMove(row, col, player, tempBoard) || violatesKoRule(tempBoard);
    }

    private boolean isSuicidalMove(int row, int col, int player, int[][] tempBoard) {
        boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];
        return !hasLiberty(row, col, player, visited, tempBoard);
    }

    private boolean hasLiberty(int x, int y, int player, boolean[][] visited, int[][] tempBoard) {
        if (x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE) return false;
        if (visited[x][y]) return false;
        if (tempBoard[x][y] == 0) return true;
        if (tempBoard[x][y] != player) return false;

        visited[x][y] = true;

        // recursively check if any of the adjacent cells has a liberty
        return hasLiberty(x-1, y, player, visited, tempBoard) ||
                hasLiberty(x+1, y, player, visited, tempBoard) ||
                hasLiberty(x, y-1, player, visited, tempBoard) ||
                hasLiberty(x, y+1, player, visited, tempBoard);
    }

    private boolean violatesKoRule(int[][] tempBoard) {
        // check if the current board state is the same as the previous board state
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (tempBoard[i][j] != previousBoardState[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public void placeStone(int row, int col, int player) {

        if (isValidMove(row, col, player)) {
            return;
        }

        previousBoardState = gameBoard.getBoardCopy();

        System.out.println("Before placing stone: Current player is " + currentPlayer); // debug
        gameBoard.setCell(row, col, player);
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        System.out.println("After placing stone: Current player is " + currentPlayer); // debug
    }

}
