import javafx.application.Application;
import javafx.stage.Stage;
import ui.ControllerLoader;

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
