package hr.gregl.gogame.game.model;

public class GameLogic {

    private final GameBoard gameBoard;
    private int currentPlayer = 1; // 1 - black; 2 - white

    public GameLogic(int size) {
        gameBoard = new GameBoard(size);
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void placeStone(int row, int col, int player) {

        System.out.println("Placing stone for player: " + player); // debug

        if (gameBoard.getCell(row, col) == 0) {
            gameBoard.setCell(row, col, player);
            currentPlayer = (currentPlayer == 1) ? 2 : 1;
        }
    }

}
