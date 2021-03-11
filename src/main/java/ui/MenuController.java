package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {
    @FXML private Button builderButton;
    @FXML private Button simulatorButton;
    @FXML private Button examplesButton;

    private Stage primaryStage;

    @FXML
    public void initialize(){
        configureUI();
    }

    public void setStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    private void configureUI(){
        builderButton.setOnAction(event -> ControllerLoader.openBuilderWindow(primaryStage));
        simulatorButton.setOnAction(event -> ControllerLoader.openSimulatorWindow(primaryStage));
    }



}
