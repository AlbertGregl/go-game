package hr.gregl.gogame.game.utility;

import javafx.stage.FileChooser;

import java.io.*;

public class GameIOUtil {

    public static void saveGame(File file, GameSaveState saveState) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(saveState);
        }
    }

    public static GameSaveState loadGame(File file) throws IOException, ClassNotFoundException {
        GameSaveState loadedState;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            loadedState = (GameSaveState) ois.readObject();
        }

        return loadedState;
    }

    public static FileChooser configureFileChooser(String title, String description, String extension) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, extension));
        return fileChooser;
    }


}
