package hr.gregl.gogame.game.utility;

import hr.gregl.gogame.game.config.GameConfig;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public class GameBoardUI {

    public static void createGameBoard(GridPane boardGrid, CellClickHandler clickHandler) {
        boardGrid.getChildren().clear();
        setBoardBorderLabels(boardGrid);

        int boardSize = GameConfig.getInstance().getBoardSize();
        int maxIndex = GameConfig.getInstance().getBoardSizeWithBorders() - 1;

        for (int i = 1; i < maxIndex; i++) {
            for (int j = 1; j < maxIndex; j++) {
                Pane pane = getPane(i, j, boardSize, clickHandler);
                boardGrid.add(pane, j, i);
            }
        }
    }

    private static Pane getPane(int i, int j, int boardSize, CellClickHandler clickHandler) {
        Pane pane = new Pane();
        pane.setPrefSize(200, 200);
        pane.setId(GameConfig.getPaneId(i, j));

        if (i == 1 && j == 1) {
            pane.setBackground(BoardImageUtil.CORNER_TOP_LEFT.getBackground());
        } else if (i == 1 && j == boardSize) {
            pane.setBackground(BoardImageUtil.CORNER_TOP_RIGHT.getBackground());
        } else if (i == boardSize && j == 1) {
            pane.setBackground(BoardImageUtil.CORNER_BOTTOM_LEFT.getBackground());
        } else if (i == boardSize && j == boardSize) {
            pane.setBackground(BoardImageUtil.CORNER_BOTTOM_RIGHT.getBackground());
        } else if (i == 1) {
            pane.setBackground(BoardImageUtil.TOP_SIDE.getBackground());
        } else if (i == boardSize) {
            pane.setBackground(BoardImageUtil.BOTTOM_SIDE.getBackground());
        } else if (j == 1) {
            pane.setBackground(BoardImageUtil.LEFT_SIDE.getBackground());
        } else if (j == boardSize) {
            pane.setBackground(BoardImageUtil.RIGHT_SIDE.getBackground());
        } else if (GameConfig.isStarPoint(i, j)) {
            pane.setBackground(BoardImageUtil.STAR.getBackground());
        } else {
            pane.setBackground(BoardImageUtil.CELL.getBackground());
        }

        pane.setOnMouseClicked(clickHandler::handleCellClick);
        return pane;
    }

    private static void setBoardBorderLabels(GridPane boardGrid) {
        for (int i = 1; i <= GameConfig.getInstance().getBoardSize(); i++) {
            Label columnLabel = new Label(String.valueOf(GameConfig.getColumnLabel(i - 1)));
            configureLabel(columnLabel);
            boardGrid.add(columnLabel, i, 0);

            Label rowLabel = new Label(String.valueOf(GameConfig.getRowLabel(i - 1)));
            configureLabel(rowLabel);
            boardGrid.add(rowLabel, 0, i);
        }
    }

    private static void configureLabel(Label label) {
        label.setAlignment(Pos.CENTER);
        label.setFont(new Font("MingLiU-ExtB", 24));
    }
}
