module hr.gregl.gogame.gogame {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens hr.gregl.gogame.game to javafx.fxml;
    exports hr.gregl.gogame.game;
}