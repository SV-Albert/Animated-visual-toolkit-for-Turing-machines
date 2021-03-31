package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;


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
