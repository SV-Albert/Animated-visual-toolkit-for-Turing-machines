package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerLoader {

    public static MenuController openMainMenu(Stage primaryStage){
        try{
            FXMLLoader menuScreenLoader = new FXMLLoader(ControllerLoader.class.getResource("../start_menu.fxml"));
            Parent root = new Pane();
            root = menuScreenLoader.load();
            MenuController controller = menuScreenLoader.getController();
            controller.setStage(primaryStage);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            return controller;
        } catch (IOException e){
            e.printStackTrace();
        }
        return new MenuController();
    }

    public static BuilderController openBuilderWindow(Stage primaryStage){
        try{
            FXMLLoader builderScreenLoader = new FXMLLoader(ControllerLoader.class.getResource("../builder.fxml"));
            Parent root = new Pane();
            root = builderScreenLoader.load();
            BuilderController controller = builderScreenLoader.getController();
            controller.setStage(primaryStage);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            return controller;
        } catch (IOException e){
            e.printStackTrace();
        }
        return new BuilderController();
    }

    public static SimulatorController openSimulatorWindow(Stage primaryStage){
        try{
            FXMLLoader simulatorScreenLoader = new FXMLLoader(ControllerLoader.class.getResource("../simulator.fxml"));
            Parent root = new Pane();
            root = simulatorScreenLoader.load();
            SimulatorController controller = simulatorScreenLoader.getController();
            controller.setStage(primaryStage);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            return controller;
        } catch (IOException e){
            e.printStackTrace();
        }
        return new SimulatorController();
    }
}
