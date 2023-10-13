package hr.gregl.gogame.game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("view/game-view.fxml"));
        Image appIcon = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/icon.png")));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/styles.css")).toExternalForm());

        stage.setTitle("いご\tGO GAME");
        stage.setScene(scene);
        stage.getIcons().add(appIcon);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}