package com.arssh.notesmanager;

import com.arssh.notesmanager.services.NotesManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Initialize NotesManager on startup
        NotesManager.getInstance().initialize();

        scene = new Scene(loadFXML("main"), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Notes Manager");
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        // Add shutdown hook to save notes on exit
        stage.setOnCloseRequest(e -> NotesManager.getInstance().shutdown());

        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}