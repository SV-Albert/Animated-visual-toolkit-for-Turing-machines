import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.BuilderController;
import ui.ControllerLoader;
import ui.MenuController;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        ControllerLoader.openMainMenu(primaryStage);
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.show();
    }
}
