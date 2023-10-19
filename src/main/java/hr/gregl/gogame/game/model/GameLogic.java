package hr.gregl.gogame.game.model;

import hr.gregl.gogame.game.config.GameConfig;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameLogic {

    private final GameBoard gameBoard;
    private int currentPlayer = 1; // 1 - black; 2 - white
    private int[][] previousBoardState;
    private final int BOARD_SIZE;
    private int blackCaptures = 0;
    private int whiteCaptures = 0;

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

    public void setBlackCaptures(int blackCaptures) {
        this.blackCaptures = blackCaptures;
    }

    public void setWhiteCaptures(int whiteCaptures) {
        this.whiteCaptures = whiteCaptures;
    }


    public void setBoard(int[][] newBoard) {
        gameBoard.setBoard(newBoard);
    }

    public int[][] getBoard() {
        return gameBoard.getBoard();
    }
    public int getBlackCaptures() {
        return blackCaptures;
    }

    public int getWhiteCaptures() {
        return whiteCaptures;
    }

    public int getCellValue(int x, int y) {
        return gameBoard.getCell(x, y);
    }

    public int getCurrentPlayer() {

        return currentPlayer;
    }

    public boolean isValidMove(int row, int col, int player) {

        if (gameBoard.hasStone(row, col)) {
            System.out.println("Cell already occupied."); // debug
            return true;
        }

        int[][] tempBoard = gameBoard.getBoardCopy();
        tempBoard[row][col] = player;

        return isSuicidalMove(row, col, player, tempBoard) || violatesKoRule(tempBoard);
    }

    public boolean canPlayerMove(int player) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (!isValidMove(i, j, player)) {
                    return false;
                }
            }
        }
        return true;
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
        return hasLiberty(x - 1, y, player, visited, tempBoard) ||
                hasLiberty(x + 1, y, player, visited, tempBoard) ||
                hasLiberty(x, y - 1, player, visited, tempBoard) ||
                hasLiberty(x, y + 1, player, visited, tempBoard);
    }

    private boolean violatesKoRule(int[][] tempBoard) {
        // "Ko" rule check if the current board state is the same as the previous board state
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
        gameBoard.setCell(row, col, player);

        List<Point> capturedStones = findCapturedStones(row, col, player);
        for (Point stone : capturedStones) {
            gameBoard.setCell(stone.x, stone.y, 0);
        }

        if (player == 1) {
            GameConfig.getInstance().decreaseBlackStones();
            blackCaptures += capturedStones.size();
        } else {
            GameConfig.getInstance().decreaseWhiteStones();
            whiteCaptures += capturedStones.size();
        }

        previousBoardState = gameBoard.getBoardCopy();
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    private List<Point> findCapturedStones(int row, int col, int player) {
        int opponent = (player == 1) ? 2 : 1;
        List<Point> capturedStones = new ArrayList<>();

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int x = row + dir[0];
            int y = col + dir[1];
            if (x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE && gameBoard.getCell(x, y) == opponent) {
                boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];
                if (!hasLiberty(x, y, opponent, visited, gameBoard.getBoardCopy())) {
                    List<Point> group = findConnectedGroup(x, y, opponent);
                    capturedStones.addAll(group);
                }
            }
        }
        return capturedStones;
    }

    private List<Point> findConnectedGroup(int x, int y, int player) {
        List<Point> group = new ArrayList<>();
        boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];
        Queue<Point> queue = new LinkedList<>();
        Point startPoint = new Point(x, y);
        queue.add(startPoint);
        visited[x][y] = true;

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            group.add(current);

            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] dir : directions) {
                int nextX = current.x + dir[0];
                int nextY = current.y + dir[1];

                if (nextX >= 0 && nextX < BOARD_SIZE && nextY >= 0 && nextY < BOARD_SIZE &&
                        !visited[nextX][nextY] && gameBoard.getCell(nextX, nextY) == player) {
                    queue.add(new Point(nextX, nextY));
                    visited[nextX][nextY] = true;
                }
            }
        }
        return group;
    }

    public int calculateScore(int player) {
        return (player == 1) ? blackCaptures : whiteCaptures;
    }

    public boolean isGameOver() {
        boolean player1CannotMove = canPlayerMove(1);
        boolean player2CannotMove = canPlayerMove(2);
        return player1CannotMove && player2CannotMove
                || GameConfig.getInstance().getBlackStonesLeft() == 0
                || GameConfig.getInstance().getWhiteStonesLeft() == 0;
    }

    public void reset() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                gameBoard.setCell(i, j, 0);
            }
        }
        currentPlayer = 1;
        blackCaptures = 0;
        whiteCaptures = 0;
        initializePreviousBoardState();
        GameConfig.getInstance().resetStones();
    }
}
