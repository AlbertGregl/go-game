package hr.gregl.gogame.game;

import hr.gregl.gogame.game.controller.GameController;
import hr.gregl.gogame.game.controller.SelectionController;
import hr.gregl.gogame.game.model.ConfigurationKey;
import hr.gregl.gogame.game.model.ConfigurationReader;
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
    private Stage primaryStage;
    private GameServer gameServer;
    private GameClient gameClient;
    private static final ConfigurationReader configReader = ConfigurationReader.getInstance();
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

        primaryStage.setOnCloseRequest(windowEvent -> {
            // release resources on exit
            if (gameServer != null) {
                gameServer.shutdownServer();
            }
            if (gameClient != null) {
                gameClient.disconnect();
            }
            Platform.exit();
            System.exit(0);
        });
    }

    public void startServer(GameStateUpdateListener listener) {
        new Thread(() -> {
            if (!isServerRunning()) {
                System.out.println(LogUtil.infoLogMsg1);
                LogUtil.logInfo(LogUtil.infoLogMsg1);
                try {
                    int serverPort = configReader.readIntegerValueForKey(ConfigurationKey.SERVER_PORT);
                    gameServer = new GameServer(serverPort, listener);
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
                int serverPort = configReader.readIntegerValueForKey(ConfigurationKey.SERVER_PORT);
                String localhost = configReader.readStringValueForKey(ConfigurationKey.HOST);
                gameClient = new GameClient(localhost, serverPort, listener);
                if (listener instanceof GameController) {
                    ((GameController) listener).setGameClient(gameClient);
                }
            } catch (IOException e) {
                LogUtil.logError(e);
            }
        }).start();
    }

    public void startGame(UserType userType) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(gameView));
                Scene scene = new Scene(loader.load());
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(stylesCss)).toExternalForm());
                Image appIcon = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(imgGameIcon)));

                GameController gameController = loader.getController();

                if (userType == UserType.SERVER) {
                    startServer(gameController);
                    primaryStage.setTitle(gameViewTitleServer);
                } else if(userType == UserType.CLIENT){
                    connectToServer(gameController);
                    primaryStage.setTitle(gameViewTitleClient);
                } else {
                    primaryStage.setTitle(gameViewTitle);
                }

                primaryStage.getIcons().add(appIcon);
                primaryStage.setScene(scene);
                primaryStage.show();
            } catch (IOException e) {
                LogUtil.logError(e);
            }
        });
    }

    public static boolean isServerRunning() {
        int serverPort = configReader.readIntegerValueForKey(ConfigurationKey.SERVER_PORT);
        try (ServerSocket ignored = new ServerSocket(serverPort)) {
            System.out.println(LogUtil.infoLogMsg3 + serverPort);
            LogUtil.logInfo(LogUtil.infoLogMsg3 + serverPort);
            return false;
        } catch (IOException e) {
            System.out.println(LogUtil.infoLogMsg4 + serverPort);
            LogUtil.logInfo(LogUtil.infoLogMsg4 + serverPort);
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
