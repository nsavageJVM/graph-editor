package com.kikul.model.editor.node;

import com.kikul.controller.KodresController;
import com.kikul.model.editor.NodeContainer;
import com.kikul.model.editor.GraphNode;
import com.kikul.model.editor.dragicon.DragIconType;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class DraggableNode extends AnchorPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(KodresController.class);

    public AnchorPane center;
    public GridPane label_node;
    public Label close_button;
    public Label title_bar;
    public AnchorPane left_link_handle;
    public AnchorPane right_link_handle;

    private EventHandler<MouseEvent> mLinkHandleDragDetected;
    private EventHandler<DragEvent> mLinkHandleDragDropped;
    private EventHandler<DragEvent> mContextLinkDragOver;
    private EventHandler<DragEvent> mContextLinkDragDropped;
    private EventHandler<DragEvent> mContextDragOver;
    private EventHandler<DragEvent> mContextDragDropped;

    private DragIconType mType = null;
    private NodeLink mDragLink = null;
    private AnchorPane right_pane = null;
    private final DraggableNode self;

    private int numberDraggableNode;
    private Point2D pointNode;
    private Point2D mDragOffset = new Point2D(0.0, 0.0);
    private final List<String> mLinkIds = new ArrayList<>();
    private final List<DraggableNode> neighboringNodes = new ArrayList<>();

    public DraggableNode() {
        this.pointNode = new Point2D(0.0, 0.0);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/DraggableNode.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        self = this;
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            LOGGER.error("Could not load specified FXML: {}", "/fxml/ErrorDialog.fxml");
            throw new RuntimeException(exception);
        }
        //provide a universally unique identifier for this object
        setId(UUID.randomUUID().toString());
    }

    public DraggableNode(int numberDraggableNode) {
        this();
        this.numberDraggableNode = numberDraggableNode;
    }

    public DraggableNode(int numberDraggableNode, String id) {
        this(numberDraggableNode);
        setId(id);
    }

    public NodeLink getMDragLink() {
        return mDragLink;
    }

    @Override
    public String toString() {
        return "D[type=" + getType() + ", name=" + numberDraggableNode + ']';
    }

    @FXML
    private void initialize() {
        buildNodeDragHandlers();
        buildLinkDragHandlers();

        left_link_handle.setOnDragDetected(mLinkHandleDragDetected);
        right_link_handle.setOnDragDetected(mLinkHandleDragDetected);
        left_link_handle.setOnDragDropped(mLinkHandleDragDropped);
        right_link_handle.setOnDragDropped(mLinkHandleDragDropped);

        mDragLink = new NodeLink();
        mDragLink.setVisible(false);
        parentProperty().addListener((observable, oldValue, newValue) -> {
            right_pane = (AnchorPane) getParent();
        });
    }

    public void setPointNode(Point2D pointNode) {
        this.pointNode = pointNode;
    }

    public Point2D getPointNode() {
        return this.pointNode;
    }

    public void registerLink(String linkId) {
        mLinkIds.add(linkId);
    }

    public List<DraggableNode> getNeighboringNodes() {
        return neighboringNodes;
    }

    public void registerLinkNode(DraggableNode node) {
        neighboringNodes.add(node);
    }

    public void removeLinkNode(List<DraggableNode> nodes) {
        for (DraggableNode node : nodes)
            node.getNeighboringNodes().remove(this);
        nodes.clear();
    }

    public int getNumberDraggableNode() {
        return numberDraggableNode;
    }

    public void relocateToPoint(Point2D p) {
        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);
        double x = (localCoords.getX() - mDragOffset.getX());
        double y = (localCoords.getY() - mDragOffset.getY());
        relocate(x, y);
        //Need for serialize links
        setPointNode(new Point2D(x, y));
    }

    public DragIconType getType() {
        return mType;
    }

    public void setType(DragIconType type) {
        mType = type;
        getStyleClass().clear();
        getStyleClass().add("drag_icon");
        switch (mType) {
            case NODE:
                title_bar.setText("N_" + getNumberDraggableNode());
                getStyleClass().add("icon-node");
                break;
            case JOINT:
                title_bar.setText("J_" + getNumberDraggableNode());
                getStyleClass().add("icon-joint");
                break;
            case GLOBAL:
                title_bar.setText("G_" + getNumberDraggableNode());
                getStyleClass().add("icon-global");
                break;
            default:
                break;
        }
    }

    public void buildNodeDragHandlers() {
        mContextDragOver = event -> {
            event.acceptTransferModes(TransferMode.ANY);
            relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
            event.consume();
        };
        dragInDropForNodeDragging();
        closeButtonClick();
        dragDetectionForNodeDragging();
    }

    private void dragDetectionForNodeDragging() {
        title_bar.setOnDragDetected(event -> {
            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);
            getParent().setOnDragOver(mContextDragOver);
            getParent().setOnDragDropped(mContextDragDropped);
            //begin drag ops
            mDragOffset = new Point2D(event.getX(), event.getY());
            relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));

            ClipboardContent content = new ClipboardContent();
            NodeContainer container = new NodeContainer();
            container.addData("type", mType.toString());
            content.put(NodeContainer.AddNode, container);

            startDragAndDrop(TransferMode.ANY).setContent(content);
            event.consume();
        });
    }

    private void closeButtonClick() {
        close_button.setOnMouseClicked(event -> {
            AnchorPane parent = (AnchorPane) self.getParent();
            removeNode(new GraphNode(self));
            parent.getChildren().remove(self);
            //iterate each link id connected to this NODE
            //find it's corresponding component in the right-hand
            //AnchorPane and delete it.
            //Note:  other nodes connected to these links are not being
            //notified that the link has been removed.
            for (ListIterator<String> iterId = mLinkIds.listIterator(); iterId.hasNext(); ) {
                String id = iterId.next();
                for (ListIterator<Node> iterNode = parent.getChildren().listIterator(); iterNode.hasNext(); ) {
                    Node node = iterNode.next();
                    if (node.getId() == null)
                        continue;
                    if (node.getId().equals(id))
                        iterNode.remove();
                }
                iterId.remove();
            }
            removeLinkNode(neighboringNodes);
        });
    }

    private void dragInDropForNodeDragging() {
        mContextDragDropped = event -> {
            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);
            event.setDropCompleted(true);
            event.consume();
        };
    }

    public boolean removeNode(GraphNode nodeToRemove) {
        for (GraphNode node : NodeContainer.graph.getNodes())
            node.getLinkedNodes().remove(nodeToRemove);
        return NodeContainer.graph.getNodes().remove(nodeToRemove);
    }

    private void buildLinkDragHandlers() {
        handleDragDetected();
        handleDragDropped();
        contextLinkDragOver();
        contextLinkDragDropped();
    }

    private void contextLinkDragDropped() {
        mContextLinkDragDropped = event -> {
            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);

            //hide the draggable NodeLink and remove it from the right-hand AnchorPane's children
            mDragLink.setVisible(false);
            right_pane.getChildren().remove(0);
            event.setDropCompleted(true);
            event.consume();
        };
    }

    private void contextLinkDragOver() {
        mContextLinkDragOver = event -> {
            event.acceptTransferModes(TransferMode.ANY);

            //Relocate end of user-draggable link
            if (!mDragLink.isVisible())
                mDragLink.setVisible(true);
            mDragLink.setEnd(new Point2D(event.getX(), event.getY()));
            event.consume();
        };
    }

    private void handleDragDropped() {
        mLinkHandleDragDropped = event -> {
            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);

            //get the drag data.  If it's null, abort.
            //This isn't the drag event we're looking for.
            NodeContainer container = (NodeContainer) event.getDragboard().getContent(NodeContainer.AddLink);
            if (container == null)
                return;

            //hide the draggable NodeLink and remove it from the right-hand AnchorPane's children
            mDragLink.setVisible(false);
            right_pane.getChildren().remove(0);
            ClipboardContent content = new ClipboardContent();

            //pass the UUID of the target NODE for later lookup
            container.addData("target", getId());
            content.put(NodeContainer.AddLink, container);
            event.getDragboard().setContent(content);
            event.setDropCompleted(true);
            event.consume();
        };
    }

    private void handleDragDetected() {
        mLinkHandleDragDetected = event -> {
            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);
            getParent().setOnDragOver(mContextLinkDragOver);
            getParent().setOnDragDropped(mContextLinkDragDropped);

            //Set up user-draggable link
            right_pane.getChildren().add(0, mDragLink);
            mDragLink.setVisible(false);
            Point2D point = new Point2D(getLayoutX() + (getWidth() / 2.0), getLayoutY() + (getHeight() / 2.0));
            mDragLink.setStart(point);

            //Drag content code
            ClipboardContent content = new ClipboardContent();
            NodeContainer container = new NodeContainer();

            //pass the UUID of the source NODE for later lookup
            container.addData("source", getId());
            content.put(NodeContainer.AddLink, container);
            startDragAndDrop(TransferMode.ANY).setContent(content);
            event.consume();
        };
    }
}
