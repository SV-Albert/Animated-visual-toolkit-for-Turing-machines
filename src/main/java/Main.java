import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ui.ControllerLoader;

public class Main extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        ControllerLoader.openMainMenu(primaryStage);
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.setTitle("TM Toolkit");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("ui/images/TM_Toolkit_Icon.png")));
        primaryStage.show();
    }
}
