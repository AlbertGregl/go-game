package hr.gregl.gogame.game.model;

public class GameBoard {

    // 0 - empty cell; 1 - black stone; 2 - white stone
    private final int[][] board;

    public GameBoard(int size) {
        board = new int[size][size];
    }

    public int getCell(int x, int y) {
        return board[x][y];
    }

    public void setCell(int x, int y, int value) {
        board[x][y] = value;
    }

}
