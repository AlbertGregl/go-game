module hr.gregl.gogame.gogame {
    requires javafx.controls;
    requires javafx.fxml;

    opens hr.gregl.gogame.game to javafx.fxml;
    exports hr.gregl.gogame.game;
}