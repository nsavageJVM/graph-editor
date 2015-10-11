package com.kikul.controller;

import com.kikul.MainEditor;
import com.kikul.controller.resources.EditorResources;
import com.kikul.model.algorithms.kodres.AbstractKodres;
import com.kikul.model.editor.NodeContainer;
import com.kikul.model.editor.GraphNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class KodresController {

    private static final Logger LOGGER = LoggerFactory.getLogger(KodresController.class);

    public Button ok;
    public TextField area;
    public TextField terminals;

    private Stage stage;
    private Parent root;
    private final AbstractKodres kodres;

    private boolean disableArea = false;
    private boolean disableTerminals = false;

    public KodresController(AbstractKodres kodres) {
        this.kodres = kodres;
        stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/KodresDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            root = fxmlLoader.load();
        } catch (Exception exception) {
            LOGGER.error("Could not load specified FXML: {}", "/fxml/KodresDialog.fxml");
            throw new RuntimeException(exception);
        }
    }

    public void showKodresDialog() {
        stage.setTitle("Set Kodres parametric");
        stage.getIcons().add(new Image("images/app_icon.png"));
        Scene scene = new Scene(root, 350, 180);
        scene.getStylesheets().addAll(MainEditor.getScene().getStylesheets());
        stage.setScene(scene);
        stage.initOwner(MainEditor.getPrimaryStage());
        stage.setResizable(false);
        stage.show();

        checkSpecifiedFields();
    }

    private void checkSpecifiedFields() {
        area.textProperty().addListener((observable, oldValue, newValue) -> {
            disableArea = !newValue.trim().isEmpty();
            setDisableOkButton();
        });
        terminals.textProperty().addListener((observable, oldValue, newValue) -> {
            disableTerminals = !newValue.trim().isEmpty();
            setDisableOkButton();
        });
    }

    private void setDisableOkButton() {
        if (disableArea && disableTerminals)
            ok.setDisable(false);
        else
            ok.setDisable(true);
    }

    public void runKodres(ActionEvent event) {
        if (area.getText().matches("\\d+") && terminals.getText().matches("\\d+")) {
            int areaInt = Integer.parseInt(area.getText());
            int terminalsInt = Integer.parseInt(terminals.getText());

            List<List<GraphNode>> resultList = kodres.start(NodeContainer.graph, areaInt, terminalsInt);
            repaintNodes(resultList);
        } else {
            // TODO не правильные значения
            LOGGER.error("Entered invalid characters. area = {}, number terminals = {}", area.getText(), terminals.getText());
            new ErrorController().showError(EditorResources.MSG_ERROR_ENTERED_INVALID_CHARACTERS);
        }
        stage.close();
    }

    private void repaintNodes(List<List<GraphNode>> resultKodres) {
        for (List<GraphNode> entry : resultKodres) {
            String color = getNewColor();
            repaintOneBlock(entry, color);
        }
    }

    private void repaintOneBlock(List<GraphNode> entry, String color) {
        for (GraphNode node : entry) {
            node.getDraggableNode().setStyle("-fx-background-color: #" + color + ";");
            node.getDraggableNode().label_node.setStyle("-fx-background-color: #" + color + ";");
            for (GraphNode link : node.getLinkedNodes()) {
                repaintLink(link);
            }
        }
    }

    private void repaintLink(GraphNode link) {
        link.getDraggableNode().setStyle("-fx-background-color: rgba(100, 100, 100, 0.2); -fx-background-image: none;");
        link.getDraggableNode().label_node.setStyle("-fx-background-color: rgba(100, 100, 100, 0.2);");
        link.getDraggableNode().left_link_handle.setStyle("-fx-background-color: rgba(100, 100, 100, 0.2);");
        link.getDraggableNode().right_link_handle.setStyle("-fx-background-color: rgba(100, 100, 100, 0.2);");
        link.getDraggableNode().center.setStyle("-fx-background-color: rgba(100, 100, 100, 0.2);");
        link.getDraggableNode().close_button.setText("");
        link.getDraggableNode().title_bar.setText("");
    }

    private String getNewColor() {
        Random random = new Random();
        Color randomColor = new Color(random.nextDouble(), random.nextDouble(), random.nextDouble(), 0.7);
        return randomColor.toString().substring(2, 8);
    }

    public void closeDialog(ActionEvent event) {
        stage.close();
    }
}
