package com.kikul;

import com.kikul.controller.resources.EditorResources;
import com.kikul.controller.KodresController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MainEditor extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(KodresController.class);

    private static Scene scene;

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        MainEditor.primaryStage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        try {
            Parent root = loader.load();
            setUpScene(root);
            setUpStage(primaryStage);
            primaryStage.show();
        } catch (IOException exception) {
            LOGGER.error("Could not load specified FXML: {}", "/fxml/ErrorDialog.fxml");
            throw new RuntimeException(exception);
        }
    }

    private void setUpScene(Parent root) {
        scene = new Scene(root, 1500, 850);
        scene.getStylesheets().add(getClass().getResource("/css/default_theme_application.css").toExternalForm());
    }

    private void setUpStage(Stage primaryStage) {
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(720);
        primaryStage.setMinHeight(480);
        primaryStage.setTitle(EditorResources.NAME_APPLICATION);
        primaryStage.getIcons().add(new Image("images/app_icon.png"));
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Scene getScene() {
        return scene;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}