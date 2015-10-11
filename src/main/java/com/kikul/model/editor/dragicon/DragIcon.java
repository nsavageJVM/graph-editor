package com.kikul.model.editor.dragicon;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DragIcon extends AnchorPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(DragIcon.class);

    private DragIconType mType;

    public DragIcon() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/DragIcon.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            LOGGER.error("Could not load specified FXML: {}", "/fxml/DragIcon.fxml");
        }
    }

    @FXML
    private void initialize() {
    }

    public void relocateToPoint(Point2D p) {
        Point2D localCoords = getParent().sceneToLocal(p);
        relocate((int) (localCoords.getX() - (getBoundsInLocal().getWidth() / 2)),
                (int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2)));
    }

    public DragIconType getType() {
        return mType;
    }

    public void setType(DragIconType type) {
        mType = type;
        getStyleClass().clear();
        getStyleClass().add("drag_icon");

        //added because the cubic curve will persist into other icons
        if (this.getChildren().size() > 0)
            getChildren().clear();
        switch (mType) {
            case NODE:
                getStyleClass().add("icon-node-panel");
                break;
            case JOINT:
                getStyleClass().add("icon-joint-panel");
                break;
            case GLOBAL:
                getStyleClass().add("icon-global-panel");
                break;
            default:
                break;
        }
    }
}