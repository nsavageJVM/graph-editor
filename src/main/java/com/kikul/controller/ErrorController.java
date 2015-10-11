package com.kikul.controller;

import com.kikul.MainEditor;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(KodresController.class);

    public ImageView img;
    public Label message;

    private Stage stage;
    private Parent root;

    public ErrorController() {
        stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ErrorDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            root = fxmlLoader.load();
        } catch (Exception exception) {
            LOGGER.error("Could not load specified FXML: {}", "/fxml/ErrorDialog.fxml");
            throw new RuntimeException(exception);
        }
    }

    public void showError(String message) {
        stage.setTitle("Error");
        stage.getIcons().add(new Image("images/app_icon.png"));
        Scene scene = new Scene(root, 500, 150);
        scene.getStylesheets().addAll(MainEditor.getScene().getStylesheets());
        stage.setScene(scene);
        stage.initOwner(MainEditor.getPrimaryStage());
        stage.setResizable(false);
        stage.show();

        img.setImage(new Image("images/error.gif"));
        this.message.setText(message);
    }

    public void closeDialog(ActionEvent event) {
        stage.close();
    }
}
