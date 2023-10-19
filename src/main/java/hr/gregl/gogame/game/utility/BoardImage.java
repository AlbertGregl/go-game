package hr.gregl.gogame.game.utility;

import hr.gregl.gogame.game.MainApplication;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.Objects;

public enum BoardImage {

    CELL("images/boardCellInner.png"),
    STAR("images/boardCellStarPoint.png"),
    TOP_SIDE("images/boardCellTopSide.png"),
    BOTTOM_SIDE("images/boardCellBottomSide.png"),
    LEFT_SIDE("images/boardCellLeftSide.png"),
    RIGHT_SIDE("images/boardCellRightSide.png"),
    CORNER_TOP_LEFT("images/boardCellCornerTopLeft.png"),
    CORNER_TOP_RIGHT("images/boardCellCornerTopRight.png"),
    CORNER_BOTTOM_LEFT("images/boardCellCornerBottomLeft.png"),
    CORNER_BOTTOM_RIGHT("images/boardCellCornerBottomRight.png");

    private final Background background;

    BoardImage(String path) {
        Image image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(path)));
        this.background = createBackgroundFromImage(image);
    }

    private static Background createBackgroundFromImage(Image image) {
        BackgroundSize size = new BackgroundSize(200, 200, false, false, false, false);
        return new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size));
    }

    public Background getBackground() {
        return this.background;
    }

}

