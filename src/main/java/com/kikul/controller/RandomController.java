package com.kikul.controller;

import com.kikul.MainEditor;
import com.kikul.controller.resources.EditorResources;
import com.kikul.controller.resources.GraphEditorException;
import com.kikul.controller.tools.RandomGraph;
import com.kikul.model.editor.NodeContainer;
import com.kikul.model.editor.RootGraph;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RandomController.class);

    public Button ok;
    public TextField nodes;
    public TextField global;
    public TextField joint;

    private Stage stage;
    private Parent root;
    private final RootLayout rootLayout;

    private boolean disableNodes = false;
    private boolean disableGlobal = false;
    private boolean disableJoint = false;

    public RandomController(RootLayout rootLayout) {
        this.rootLayout = rootLayout;
        stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/RandomDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            root = fxmlLoader.load();
        } catch (Exception exception) {
            LOGGER.error("Could not load specified FXML: {}", "/fxml/RandomDialog.fxml");
            throw new RuntimeException(exception);
        }
    }

    public void showRandomDialog() {
        stage.setTitle("Set parameters for random graph");
        stage.getIcons().add(new Image("images/app_icon.png"));
        Scene scene = new Scene(root, 350, 200);
        scene.getStylesheets().addAll(MainEditor.getScene().getStylesheets());
        stage.setScene(scene);
        stage.initOwner(MainEditor.getPrimaryStage());
        stage.setResizable(false);
        stage.show();

        checkSpecifiedFields();
    }

    private void checkSpecifiedFields() {
        nodes.textProperty().addListener((observable, oldValue, newValue) -> {
            disableNodes = !newValue.trim().isEmpty();
            setDisableOkButton();
        });
        global.textProperty().addListener((observable, oldValue, newValue) -> {
            disableGlobal = !newValue.trim().isEmpty();
            setDisableOkButton();
        });
        joint.textProperty().addListener((observable, oldValue, newValue) -> {
            disableJoint = !newValue.trim().isEmpty();
            setDisableOkButton();
        });
    }

    private void setDisableOkButton() {
        if (disableNodes && disableGlobal && disableJoint)
            ok.setDisable(false);
        else
            ok.setDisable(true);
    }

    public void runRandom(ActionEvent event) {
        try {
            if (nodes.getText().matches("\\d+") && global.getText().matches("\\d+")
                    && joint.getText().matches("\\d+")) {
                int numNodes = Integer.parseInt(nodes.getText());
                int numJoint = Integer.parseInt(joint.getText());
                int numGlobal = Integer.parseInt(global.getText());

                validateSpecifiedNodes(numNodes, numJoint, numGlobal);

                rootLayout.right_pane.getChildren().clear();
                NodeContainer.graph = new RootGraph();
                RandomGraph graphConnect = new RandomGraph(NodeContainer.graph, numNodes, numGlobal, numJoint);
                Platform.runLater(() -> graphConnect.randomGraph(rootLayout));
            } else {
                LOGGER.error("Entered invalid characters. number nodes = {}, number global = {}, number joint = {}",
                        nodes.getText(), global.getText(), joint.getText());
                new ErrorController().showError(EditorResources.MSG_ERROR_ENTERED_INVALID_CHARACTERS);
            }
        } catch (GraphEditorException exception) {
            new ErrorController().showError(exception.getMessage());
        } finally {
            stage.close();
        }
    }

    private void validateSpecifiedNodes(int numNodes, int numJoint, int numGlobal) {
        if (numNodes < 2 || numJoint < 2 || numGlobal < 2) {
            throw new GraphEditorException(EditorResources.MSG_ERROR_NUMBER_OF_EACH_NODE_SHOULD_BE_AT_LEAST_TWO);
        }
    }

    public void closeDialog(ActionEvent event) {
        stage.close();
    }
}
