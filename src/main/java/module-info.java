module hr.gregl.gogame.gogame {
    requires javafx.controls;
    requires javafx.fxml;

    exports hr.gregl.gogame.game;
    opens hr.gregl.gogame.game to javafx.fxml;
}