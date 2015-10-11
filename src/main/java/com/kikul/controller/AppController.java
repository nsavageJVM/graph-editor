package com.kikul.controller;

import com.kikul.MainEditor;
import com.kikul.controller.resources.EditorResources;
import com.kikul.controller.resources.GraphEditorException;
import com.kikul.controller.resources.StyleApplication;
import com.kikul.controller.tools.GraphSerialize;
import com.kikul.model.algorithms.dfs.DFS;
import com.kikul.model.algorithms.kodres.AbstractKodres;
import com.kikul.model.algorithms.kodres.DecompositionKodres;
import com.kikul.model.algorithms.kodres.SimpleKodres;
import com.kikul.model.editor.NodeContainer;
import com.kikul.model.editor.GraphNode;
import com.kikul.model.editor.dragicon.DragIconType;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;

import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    public Button toolClear;
    public Button toolOpen;
    public Button toolSave;
    public Button toolRandom;
    public Button toolKodres;

    public MenuItem open;
    public MenuItem clear;
    public MenuItem save;
    public MenuItem random;
    public MenuItem exit;

    public RadioMenuItem grid;
    public RadioMenuItem default_colors;
    public RadioMenuItem dark_colors;
    public MenuItem kodres;

    public MenuItem decomposition;
    public MenuItem dfs;

    public RootLayout rootLayout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setStyle();
        GraphSerialize graph = new GraphSerialize(rootLayout);
        toolButtonSetOnAction(graph);
        menuItemSetOnAction(graph);
        setGraphicMenuItem();
        setHotKey();
    }

    private void setStyle() {
        StyleApplication styleApplication = new StyleApplication(rootLayout, grid);
        default_colors.setSelected(true);
        grid.setSelected(false);
        styleApplication.settingsTheme(default_colors, dark_colors);
        styleApplication.settingsGrid();
    }

    private void menuItemSetOnAction(GraphSerialize graph) {
        clear.setOnAction(event -> clearWorkspace());
        open.setOnAction(event -> graph.loadGraphFromXML());
        save.setOnAction(event -> graph.saveGraphToXML());
        random.setOnAction(event -> randomNewGraph());
        exit.setOnAction(actionEvent -> Platform.exit());

        kodres.setOnAction(event -> setKodresParametric());
        decomposition.setOnAction(event -> setDecompositionKodresParametric());
        dfs.setOnAction(event -> new DFS(NodeContainer.graph).start());
    }

    private void toolButtonSetOnAction(GraphSerialize graph) {
        toolClear.setOnAction(event -> clearWorkspace());
        toolOpen.setOnAction(event -> graph.loadGraphFromXML());
        toolSave.setOnAction(event -> graph.saveGraphToXML());
        toolRandom.setOnAction(event -> randomNewGraph());
        toolKodres.setOnAction(event -> setKodresParametric());
    }

    private void clearWorkspace() {
        rootLayout.right_pane.getChildren().clear();
        MainEditor.getPrimaryStage().setTitle(EditorResources.NAME_APPLICATION);
        NodeContainer.graph.clear();
    }

    private void setGraphicMenuItem() {
        save.setGraphic(new ImageView("images/menu/save.png"));
        open.setGraphic(new ImageView("images/menu/open.png"));
        grid.setGraphic(new ImageView("images/menu/grid.png"));
        toolOpen.setGraphic(new ImageView("images/menu/open.png"));
        toolSave.setGraphic(new ImageView("images/menu/save.png"));
        toolClear.setGraphic(new ImageView("images/menu/clear.png"));
        toolRandom.setGraphic(new ImageView("images/menu/cubic.png"));
    }

    private void setHotKey() {
        save.setAccelerator(KeyCombination.keyCombination("shortcut+s"));
        open.setAccelerator(KeyCombination.keyCombination("shortcut+o"));
        exit.setAccelerator(KeyCombination.keyCombination("alt+f4"));
    }

    private void randomNewGraph() {
        new RandomController(rootLayout).showRandomDialog();
    }

    private void setKodresParametric() {
        try {
            validateGraph();
            AbstractKodres simpleKodres = new SimpleKodres();
            KodresController kodresController = new KodresController(simpleKodres);
            kodresController.showKodresDialog();
        } catch (GraphEditorException exception) {
            new ErrorController().showError(exception.getMessage());
        }
    }

    private void setDecompositionKodresParametric() {
        try {
            validateGraph();
            AbstractKodres decompositionKodres = new DecompositionKodres();
            KodresController kodresController = new KodresController(decompositionKodres);
            kodresController.showKodresDialog();
        } catch (GraphEditorException exception) {
            new ErrorController().showError(exception.getMessage());
        }
    }

    private void validateGraph() {
        if (NodeContainer.graph.isEmpty())
            throw new GraphEditorException(EditorResources.MSG_ERROR_GRAPH_IS_EMPTY);
        else if (nodesLinkIsEmpty())
            throw new GraphEditorException(EditorResources.MSG_ERROR_NODES_HAVE_NO_CONNECTIONS);
        else if (globalLinkIsEmpty())
            throw new GraphEditorException(EditorResources.MSG_ERROR_GLOBAL_DOES_NOT_HAVE_LINK);
        else if (jointLinkIsLessTwo())
            throw new GraphEditorException(EditorResources.MSG_ERROR_JOINT_HAVE_LESS_TWO_CONNECTIONS);
        else if (globalLinkIsNotNode())
            throw new GraphEditorException(EditorResources.MSG_ERROR_GLOBAL_LINKS_IS_NOT_NODE);
    }

    private boolean nodesLinkIsEmpty() {
        for (GraphNode node : NodeContainer.graph.getNodesByType(DragIconType.NODE))
            if (NodeContainer.graph.getNodesLink(node).isEmpty())
                return true;
        return false;
    }

    private boolean globalLinkIsEmpty() {
        for (GraphNode node : NodeContainer.graph.getNodesByType(DragIconType.GLOBAL))
            if (NodeContainer.graph.getNodesLink(node).isEmpty())
                return true;
        return false;
    }

    private boolean jointLinkIsLessTwo() {
        for (GraphNode node : NodeContainer.graph.getNodesByType(DragIconType.JOINT))
            if (NodeContainer.graph.getNodesLink(node).size() < 2)
                return true;
        return false;
    }

    private boolean globalLinkIsNotNode() {
        for (GraphNode global : NodeContainer.graph.getNodesByType(DragIconType.GLOBAL))
            for (GraphNode node : NodeContainer.graph.getNodesLink(global))
                if (!node.getType().equals(DragIconType.NODE))
                    return true;
        return false;
    }

}
