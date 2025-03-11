package com.tems;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Init
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainView.fxml"));
            BorderPane root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("TEMS Application");

            // Fullscreen
            primaryStage.setFullScreen(true); 
            primaryStage.setFullScreenExitHint("");

            // Show
            primaryStage.show();
        } catch (Exception e) { 
            System.err.println("An error occurred during startup:");
            e.printStackTrace();
            System.exit(1); 
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
