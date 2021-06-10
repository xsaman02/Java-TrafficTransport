package vut.fit.ija.main.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import vut.fit.ija.main.Log;
import vut.fit.ija.main.controller.Controller;

/**
 * FXMain
 */
public class FXMain extends Application{

    public static void main(String[] args) {
        Log.setLogger();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main_window.fxml"));
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double height = screenBounds.getHeight();
        double width = screenBounds.getWidth() * 0.75;
        stage.setTitle("Traffic Simulation");
        stage.setScene(new Scene(root, width, height));
        stage.show();
    }
}