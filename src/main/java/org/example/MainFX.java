package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFX extends Application {
    private static final Logger LOGGER = Logger.getLogger(MainFX.class.getName());

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HOME.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("HOME");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load FXML file", e);
            throw new RuntimeException("Failed to load FXML file", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during application startup", e);
            throw new RuntimeException("Unexpected error during application startup", e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}