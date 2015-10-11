package com.kikul.controller.tools;

import com.kikul.MainEditor;
import com.kikul.controller.resources.EditorResources;
import com.kikul.controller.ErrorController;
import com.kikul.controller.RootLayout;
import com.kikul.model.editor.NodeContainer;
import com.kikul.model.editor.GraphNode;
import com.kikul.model.editor.RootGraph;
import com.kikul.model.editor.node.DraggableNode;
import com.kikul.model.editor.node.NodeLink;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class GraphSerialize {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphSerialize.class);

    private final RootLayout rootLayout;

    public GraphSerialize(RootLayout rootLayout) {
        this.rootLayout = rootLayout;
    }

    public void loadGraphFromXML() {
        File file = chooseFile();
        if (file != null) {
            convertXMLToObject(file);
            setApplicationTitle(file);
        }
    }

    private File chooseFile() {
        FileChooser fileChooser = getFileChooser("Open Graph");
        setExtFilters(fileChooser);
        return fileChooser.showOpenDialog(rootLayout.getScene().getWindow());
    }

    public void convertXMLToObject(File file) {
        try {
            rootLayout.right_pane.getChildren().clear();
            JAXBContext context = JAXBContext.newInstance(RootGraph.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            NodeContainer.setGraph((RootGraph) unmarshaller.unmarshal(file));
            deSerialNodes();
            deSerialLinkBetweenNodes();
        } catch (JAXBException e) {
            LOGGER.error("Failed to convert the graph from file({})", file.getName());
            new ErrorController().showError(EditorResources.MSG_ERROR_FAILED_TO_CONVERT_THE_GRAPH_FROM_FILE);
        }
    }

    private void setApplicationTitle(File file) {
        String title = file.getName().substring(0, file.getName().indexOf("."))
                + " - [" + file.getParent() + "] - " + EditorResources.NAME_APPLICATION;
        MainEditor.getPrimaryStage().setTitle(title);
    }

    public void saveGraphToXML() {
        FileChooser fileChooser = getFileChooser("Save Graph");
        fileChooser.setInitialFileName(EditorResources.DEFAULT_NAME_NEW_FILE);
        File file = fileChooser.showSaveDialog(rootLayout.getScene().getWindow());
        if (file != null)
            convertObjectToXml(file);
    }

    private FileChooser getFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        setExtFilters(fileChooser);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle(title);
        return fileChooser;
    }

    private void setExtFilters(FileChooser chooser) {
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML", "*.xml"));
    }

    public void convertObjectToXml(File file) {
        try {
            if (file.exists())
                file.delete();
            JAXBContext jaxbContext = JAXBContext.newInstance(RootGraph.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(NodeContainer.graph, file);
        } catch (JAXBException e) {
            LOGGER.error("Failed convert object to xml {}", file.getName());
            new ErrorController().showError(EditorResources.MSG_ERROR_FAILED_CONVERT_OBJECT_TO_XML);
        }
    }

    private void deSerialNodes() {
        for (GraphNode graphNode : NodeContainer.graph.getNodes()) {
            DraggableNode node = new DraggableNode(graphNode.getNumNode(), graphNode.getId());
            node.setType(graphNode.getType());
            rootLayout.setNumberNode(graphNode.getType(), graphNode.getNumNode());
            rootLayout.right_pane.getChildren().add(node);

            Point2D cursorPoint = graphNode.getCursorPoint();
            node.setPointNode(cursorPoint);
            node.relocate(cursorPoint.getX(), cursorPoint.getY());
            graphNode.setNode(node);
        }
    }


    private void deSerialLinkBetweenNodes() {
        for (GraphNode graphNode : NodeContainer.graph.getNodes()) {
            for (GraphNode link : graphNode.getLinkedNodes()) {
                NodeLink nodeLink = new NodeLink();
                rootLayout.right_pane.getChildren().add(0, nodeLink);
                DraggableNode dragSource = null;
                DraggableNode dragTarget = null;

                for (Node node : rootLayout.right_pane.getChildren()) {
                    if (node.getId() == null)
                        continue;
                    if (node.getId().equals(graphNode.getId()))
                        dragSource = (DraggableNode) node;
                    if (node.getId().equals(link.getId()))
                        dragTarget = (DraggableNode) node;
                }
                link.setNode(dragTarget);
                if (dragSource != null && dragTarget != null)
                    nodeLink.bindEnds(dragSource, dragTarget);
            }
        }
    }
}
