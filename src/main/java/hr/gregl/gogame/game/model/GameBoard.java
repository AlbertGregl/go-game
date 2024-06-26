package hr.gregl.gogame.game.model;

public class GameBoard {

    // 0 - empty cell; 1 - black stone; 2 - white stone
    private final int[][] board;

    public GameBoard(int size) {
        this.board = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = 0; // empty cell
            }
        }
    }

    public void setBoard(int[][] newBoard) {
        if (newBoard != null && newBoard.length == board.length && newBoard[0].length == board[0].length) {
            for (int i = 0; i < board.length; i++) {
                System.arraycopy(newBoard[i], 0, board[i], 0, board[i].length);
            }
        } else {
            throw new IllegalArgumentException("Invalid board state");
        }
    }

    public int getCell(int x, int y) {
        return board[x][y];
    }

    public void setCell(int x, int y, int value) {
        board[x][y] = value;
    }

    public boolean hasStone(int x, int y) {
        return board[x][y] != 0;
    }

    public int[][] getBoardCopy() {
        int[][] copy = new int[board.length][];
        for (int i = 0; i < board.length; i++) {
            copy[i] = board[i].clone();
        }
        return copy;
    }

}
