package com.kikul.controller.tools;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;

import java.io.IOException;

public class Zoom extends AnchorPane {

    private DoubleProperty zoomFactor = new SimpleDoubleProperty(1);
    private Scale scale;
    private Node content;

    public Zoom() {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ZoomPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setContent(AnchorPane pane) {
        scale = new Scale(1, 1);
        content = pane;
        pane.getTransforms().add(scale);
        zoomFactor.addListener((observable, oldValue, newValue) -> {
            scale.setX(newValue.doubleValue());
            scale.setY(newValue.doubleValue());
            requestLayout();
        });
    }

    protected void layoutChildren() {
        Pos pos = Pos.TOP_LEFT;
        double contentWidth = (getWidth() - getInsets().getLeft() - getInsets().getRight()) / zoomFactor.get();
        double contentHeight = (getHeight() - getInsets().getTop() - getInsets().getBottom()) / zoomFactor.get();
        layoutInArea(content, getInsets().getLeft(), getInsets().getTop(),
                contentWidth, contentHeight, 0, null,
                pos.getHpos(), pos.getVpos());
    }

    public final DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }
}

