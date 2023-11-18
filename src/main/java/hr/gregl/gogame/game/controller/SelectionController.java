package hr.gregl.gogame.game.controller;

import hr.gregl.gogame.game.MainApplication;
import hr.gregl.gogame.game.model.UserType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class SelectionController {
    private MainApplication mainApp;
    @FXML
    protected void startServer(ActionEvent event) {
        MainApplication.setUserType(UserType.SERVER);
        closeStage(event);
    }
    @FXML
    protected void startClient(ActionEvent event) {
        MainApplication.setUserType(UserType.CLIENT);
        closeStage(event);
    }
    @FXML
    public void startSinglePlayer(ActionEvent actionEvent) {
        MainApplication.setUserType(UserType.SINGLE_PLAYER);
        closeStage(actionEvent);
    }

    public void setMainApplication(MainApplication mainApp) {
        this.mainApp = mainApp;
    }
    
    private void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
        mainApp.startGame(UserType.valueOf(MainApplication.getUserType().toString()));
    }
}