package com.kikul.model.editor;

import javafx.scene.input.DataFormat;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NodeContainer implements Serializable {

    private static final long serialVersionUID = -1890998765646621338L;

    public static final DataFormat AddNode = new DataFormat("application.dragicon.DragIcon.add");
    public static final DataFormat DragNode = new DataFormat("application.node.DraggableNode.drag");
    public static final DataFormat AddLink = new DataFormat("application.node.NodeLink.add");

    public static RootGraph graph = new RootGraph();

    private final List<Pair<String, Object>> mDataPairs = new ArrayList<>();

    public void addData(String key, Object value) {
        mDataPairs.add(new Pair<>(key, value));
    }

    public <T> T getValue(String key) {
        for (Pair<String, Object> data : mDataPairs) {
            if (data.getKey().equals(key))
                return (T) data.getValue();
        }
        return null;
    }

    public static void setGraph(RootGraph rootGraph) {
        graph = rootGraph;
    }

    public List<Pair<String, Object>> getData() {
        return mDataPairs;
    }
}