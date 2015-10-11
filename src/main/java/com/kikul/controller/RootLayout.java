package com.kikul.controller;

import com.kikul.controller.tools.Zoom;
import com.kikul.model.editor.NodeContainer;
import com.kikul.model.editor.GraphNode;
import com.kikul.model.editor.dragicon.DragIcon;
import com.kikul.model.editor.dragicon.DragIconType;
import com.kikul.model.editor.node.DraggableNode;
import com.kikul.model.editor.node.NodeLink;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RootLayout extends AnchorPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootLayout.class);

    @FXML
    Zoom zoom;
    @FXML
    ScrollPane scroll;
    @FXML
    public SplitPane base_pane;
    @FXML
    public AnchorPane right_pane;
    @FXML
    public VBox left_pane;

    private DragIcon mDragOverIcon = null;
    private EventHandler mIconDragOverRoot = null;
    private EventHandler mIconDragDropped = null;
    private EventHandler mIconDragOverRightPane = null;

    private int numberNode = 1;
    private int numberGlobal = 1;
    private int numberJoint = 1;

    public RootLayout() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/RootLayout.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            LOGGER.error("Could not load specified FXML: {}", "/fxml/RootLayout.fxml");
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void initialize() {
        //Add one icon that will be used for the drag-drop process
        //This is added as a child to the root AnchorPane so it can be
        //visible on both sides of the split pane.
        mDragOverIcon = new DragIcon();
        mDragOverIcon.setVisible(false);
        mDragOverIcon.setOpacity(0.65);
        getChildren().add(mDragOverIcon);

        for (int i = 0; i < 3; i++) {
            DragIcon icon = new DragIcon();
            addDragDetection(icon);
            icon.setType(DragIconType.values()[i]);
            Label label = new Label(icon.getType().getValue());
            left_pane.getChildren().addAll(label, icon);
        }

        buildDragHandlers();
        zoom.setContent(right_pane);
        Slider slider = new Slider(0.1, 1.5, 1);
        slider.setVisible(false);
        scroll.setOnKeyPressed(event -> {
            if ((event.isControlDown() && event.getCode() == KeyCode.ADD)) {
                slider.setValue(slider.getValue() + 0.1);
            } else if (event.isControlDown() && event.getCode() == KeyCode.SUBTRACT) {
                slider.setValue(slider.getValue() - 0.1);
            }
        });
        zoom.zoomFactorProperty().bind(slider.valueProperty());
    }

    private void addDragDetection(DragIcon dragIcon) {
        dragIcon.setOnDragDetected(event -> {
            // set the other drag event handles on their respective objects
            base_pane.setOnDragOver(mIconDragOverRoot);
            right_pane.setOnDragOver(mIconDragOverRightPane);
            right_pane.setOnDragDropped(mIconDragDropped);
            // get a reference to the clicked com.kikul.model.editor.dragicon.DragIcon object
            DragIcon icn = (DragIcon) event.getSource();
            //begin drag ops
            mDragOverIcon.setType(icn.getType());
            mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));

            ClipboardContent content = new ClipboardContent();
            NodeContainer container = new NodeContainer();
            container.addData("type", mDragOverIcon.getType().toString());
            content.put(NodeContainer.AddNode, container);

            mDragOverIcon.startDragAndDrop(TransferMode.ANY).setContent(content);
            mDragOverIcon.setVisible(true);
            mDragOverIcon.setMouseTransparent(true);
            event.consume();
        });
    }

    private void buildDragHandlers() {
        //drag over transition to move widget form left pane to right pane
        mIconDragOverRoot = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Point2D p = right_pane.sceneToLocal(event.getSceneX(), event.getSceneY());
                //turn on transfer mode and track in the right-pane's context
                //if (and only if) the mouse cursor falls within the right pane's bounds.
                if (!right_pane.boundsInLocalProperty().get().contains(p)) {
                    event.acceptTransferModes(TransferMode.ANY);
                    mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                    return;
                }
                event.consume();
            }
        };
        mIconDragOverRightPane = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                //convert the mouse coordinates to scene coordinates,
                //then convert back to coordinates that are relative to
                //the parent of mDragIcon.  Since mDragIcon is a child of the root
                //pane, coordinates must be in the root pane's coordinate system to work
                //properly.
                mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                event.consume();
            }
        };
        mIconDragDropped = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                NodeContainer container = (NodeContainer) event.getDragboard().getContent(NodeContainer.AddNode);
                container.addData("scene_coords", new Point2D(event.getSceneX(), event.getSceneY()));
                ClipboardContent content = new ClipboardContent();
                content.put(NodeContainer.AddNode, container);
                event.getDragboard().setContent(content);
                event.setDropCompleted(true);
            }
        };
        this.setOnDragDone(event -> {
            right_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRightPane);
            right_pane.removeEventHandler(DragEvent.DRAG_DROPPED, mIconDragDropped);
            base_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRoot);
            mDragOverIcon.setVisible(false);

            NodeContainer container = (NodeContainer) event.getDragboard().getContent(NodeContainer.AddNode);
            if (container != null) {
                if (container.getValue("scene_coords") != null) {
                    DragIconType type = DragIconType.valueOf(container.getValue("type"));
                    DraggableNode node = new DraggableNode(getNumberNode(type));
                    node.setType(type);
                    right_pane.getChildren().add(node);

                    Point2D cursorPoint = container.getValue("scene_coords");
                    node.setPointNode(cursorPoint);
                    node.relocateToPoint(new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32));

                    //SERIALIZE NODE FROM DRAGBOARD
                    NodeContainer.graph.addNode(new GraphNode(node));
                }
            }
            container = (NodeContainer) event.getDragboard().getContent(NodeContainer.AddLink);
            if (container != null) {
                //bind the ends of our link to the nodes whose id's are stored in the drag container
                String sourceId = container.getValue("source");
                String targetId = container.getValue("target");
                if (sourceId != null && targetId != null)
                    linkTwoNodes(sourceId, targetId);
            }
            event.consume();
        });
    }

    public void linkTwoNodes(String sourceId, String targetId) {
        NodeLink link = new NodeLink();

        //add our link at the top of the rendering order so it's rendered first
        right_pane.getChildren().add(0, link);
        DraggableNode source = null;
        DraggableNode target = null;
        for (Node node : this.right_pane.getChildren()) {
            if (node.getId() == null)
                continue;
            if (node.getId().equals(sourceId))
                source = (DraggableNode) node;
            if (node.getId().equals(targetId))
                target = (DraggableNode) node;
        }

        //Serialize links between nodes
        for (GraphNode graphNode : NodeContainer.graph.getNodes()) {
            if (graphNode.getId().equals(targetId)) {
                GraphNode graphSource = new GraphNode(source);
                if (!graphNode.getLinkedNodes().contains(graphSource))
                    graphNode.addLinkedNodes(graphSource);
            }
            if (graphNode.getId().equals(sourceId)) {
                GraphNode graphTarget = new GraphNode(target);
                if (!graphNode.getLinkedNodes().contains(graphTarget))
                    graphNode.addLinkedNodes(graphTarget);
            }
        }
        if (source != null && target != null)
            link.bindEnds(source, target);
    }

    private int getNumberNode(DragIconType type) {
        int name;
        switch (type) {
            case NODE:
                name = numberNode++;
                break;
            case GLOBAL:
                name = numberGlobal++;
                break;
            case JOINT:
                name = numberJoint++;
                break;
            default:
                name = -1;
        }
        return name;
    }

    public void setNumberNode(DragIconType type, int num) {
        ++num;
        switch (type) {
            case NODE:
                numberNode = num;
                break;
            case GLOBAL:
                numberGlobal = num;
                break;
            case JOINT:
                numberJoint = num;
                break;
        }
    }
}