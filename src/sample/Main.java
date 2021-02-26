package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderRepeat;
import javafx.stage.Stage;

import java.lang.annotation.Target;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("pacman.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("PacMan");
        Controller controller = loader.getController();
        root.setOnKeyPressed(controller);
        double sceneWidth = controller.getBoardWidth() + 20.0;
        double sceneHeight = controller.getBoardHeight() + 100.0;
        primaryStage.setScene(new Scene(root, sceneWidth, sceneHeight));
        primaryStage.show();
        root.requestFocus();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
