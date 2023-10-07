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
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Image appIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/icon.png")));
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);

        stage.setTitle("いご Go");
        stage.setScene(scene);
        stage.getIcons().add(appIcon);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}