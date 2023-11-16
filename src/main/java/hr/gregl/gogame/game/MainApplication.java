package hr.gregl.gogame.game;

import hr.gregl.gogame.game.controller.GameController;
import hr.gregl.gogame.game.controller.SelectionController;
import hr.gregl.gogame.game.model.UserType;
import hr.gregl.gogame.game.networking.GameClient;
import hr.gregl.gogame.game.networking.GameServer;
import hr.gregl.gogame.game.networking.GameStateUpdateListener;
import hr.gregl.gogame.game.utility.LogUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;

public class MainApplication extends Application {

    public static UserType userType;
    private static final int SERVER_PORT = 49155;
    private static final String LOCALHOST = "localhost";
    private Stage primaryStage;
    private GameServer gameServer;
    private GameClient gameClient;
    private static final String selectionView = "view/selection-view.fxml";
    private static final String selectionViewTitle = "Select Server or Client";
    private static final String gameView = "view/game-view.fxml";
    private static final String gameViewTitle = "いご\tGO GAME";
    private static final String gameViewTitleServer = gameViewTitle + " - SERVER";
    private static final String gameViewTitleClient = gameViewTitle + " - CLIENT";
    private static final String stylesCss = "styles/styles.css";
    private static final String imgGameIcon = "images/icon.png";

    public static UserType getUserType() {
        return userType;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(selectionView));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle(selectionViewTitle);

        SelectionController selectionController = loader.getController();
        selectionController.setMainApplication(this);

        primaryStage.show();
    }

    public void startServer(GameStateUpdateListener listener) {
        new Thread(() -> {
            if (!isServerRunning()) {
                System.out.println(LogUtil.infoLogMsg1);
                LogUtil.logInfo(LogUtil.infoLogMsg1);
                try {
                    gameServer = new GameServer(SERVER_PORT, listener);
                    if (listener instanceof GameController) {
                        ((GameController) listener).setGameServer(gameServer);
                    }
                } catch (IOException e) {
                    LogUtil.logError(e);
                }
            } else {
                System.out.println(LogUtil.warningLogMsg1);
                LogUtil.logWarning(LogUtil.warningLogMsg1);
            }
        }).start();
    }

    public void connectToServer(GameStateUpdateListener listener) {
        new Thread(() -> {
            System.out.println(LogUtil.infoLogMsg2);
            LogUtil.logInfo(LogUtil.infoLogMsg2);
            try {
                gameClient = new GameClient(LOCALHOST, SERVER_PORT, listener);
                if (listener instanceof GameController) {
                    ((GameController) listener).setGameClient(gameClient);
                }
            } catch (IOException e) {
                LogUtil.logError(e);
            }
        }).start();
    }

    public void startGame(UserType userType) {
        Platform.runLater(() -> { // starting a new JavaFX application from a non-JavaFX thread
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(gameView));
                Scene scene = new Scene(loader.load());
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(stylesCss)).toExternalForm());
                Image appIcon = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(imgGameIcon)));

                GameController gameController = loader.getController();

                if (userType == UserType.SERVER) {
                    startServer(gameController);
                } else {
                    connectToServer(gameController);
                }

                primaryStage.getIcons().add(appIcon);
                primaryStage.setTitle(userType == UserType.SERVER ? gameViewTitleServer : gameViewTitleClient);
                primaryStage.setScene(scene);
                primaryStage.show();
            } catch (IOException e) {
                LogUtil.logError(e);
            }
        });
    }

    public static boolean isServerRunning() {
        try (ServerSocket ignored = new ServerSocket(SERVER_PORT)) {
            System.out.println(LogUtil.infoLogMsg3 + SERVER_PORT);
            LogUtil.logInfo(LogUtil.infoLogMsg3 + SERVER_PORT);
            return false;
        } catch (IOException e) {
            System.out.println(LogUtil.infoLogMsg4 + SERVER_PORT);
            LogUtil.logInfo(LogUtil.infoLogMsg4 + SERVER_PORT);
            return true;
        }
    }

    public static void setUserType(UserType type) {
        userType = type;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
