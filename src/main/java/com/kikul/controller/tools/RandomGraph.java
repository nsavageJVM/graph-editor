package com.kikul.controller.tools;

import com.kikul.controller.RootLayout;
import com.kikul.model.editor.NodeContainer;
import com.kikul.model.editor.GraphNode;
import com.kikul.model.editor.RootGraph;
import com.kikul.model.editor.dragicon.DragIconType;
import com.kikul.model.editor.node.DraggableNode;
import javafx.geometry.Point2D;

import java.util.List;
import java.util.Random;

public class RandomGraph {

    private RootGraph graph;
    private RootLayout rootLayout;

    private int numberNode = 1;
    private int numberGlobal = 1;
    private int numberJoint = 1;

    private int approximateNode;
    private int approximateGlobal;
    private int approximateJoint;

    private int coordX = 50;
    private int increment = 100;

    private final Random random = new Random();

    public RandomGraph(RootGraph graph, int approximateNode, int approximateGlobal, int approximateJoint) {
        this.graph = graph;
        this.approximateNode = approximateNode;
        this.approximateGlobal = approximateGlobal;
        this.approximateJoint = approximateJoint;
    }

    private void randomColumn(int numberNode, int coordX, DragIconType type) {
        for (int i = 0; i < numberNode; i++) {
            DraggableNode node = new DraggableNode(getNumberNode(type));
            node.setType(type);

            int coordY = 100;
            Point2D cursorPoint = new Point2D(coordX, coordY * i + 35);
            node.setPointNode(cursorPoint);
            GraphNode graphNode = new GraphNode(node);
            graphNode.setId(graphNode.getType() + "&" + graphNode.getNumNode()
                    + "&x=" + graphNode.getX() + "&y=" + graphNode.getY());

            NodeContainer.graph.addNode(graphNode);
            node.setId(graphNode.getId());
            rootLayout.right_pane.getChildren().add(node);
            node.relocate(cursorPoint.getX(), cursorPoint.getY());
        }
    }

    private void randomLink() {
        List<GraphNode> nodeList = graph.getNodesByType(DragIconType.NODE);
        List<GraphNode> globalList = graph.getNodesByType(DragIconType.GLOBAL);
        List<GraphNode> jointList = graph.getNodesByType(DragIconType.JOINT);

        int range = 3;
        linkNodesByType(globalList, nodeList, range, 1);
        linkNodesByType(nodeList, jointList, range, 1);
        linkNodesByType(jointList, nodeList, range, 2);
    }

    private void linkNodesByType(List<GraphNode> thatConnect, List<GraphNode> whereSearchLink, int range, int minLink) {
        int numberLink;
        for (GraphNode node : thatConnect) {
            numberLink = random.nextInt(range - minLink) + minLink - node.getLinkedNodes().size();
            for (int i = 0; i < numberLink; i++) {
                GraphNode link = findNodeForLink(whereSearchLink, node, 2 * increment);
                rootLayout.linkTwoNodes(node.getId(), link.getId());
            }
        }
    }

    private GraphNode findNodeForLink(List<GraphNode> whereSearchLink, GraphNode thatBind, int scaleSearch) {
        GraphNode link = getRandomList(whereSearchLink);
        if (link.getLinkedNodes().contains(link) || Math.abs(link.getX() - thatBind.getX()) > scaleSearch) {
            return findNodeForLink(whereSearchLink, thatBind, scaleSearch);
        }
        return link;
    }

    public GraphNode getRandomList(List<GraphNode> list) {
        int index = random.nextInt(list.size());
        return list.get(index);
    }

    private DragIconType[] randomType(int numberColumn) {
        DragIconType[] iconTypes = new DragIconType[numberColumn];
        for (int i = 1; i < numberColumn - 1; i++) {
            iconTypes[i] = (i % 2 == 0) ? DragIconType.JOINT : DragIconType.NODE;
        }
        iconTypes[0] = DragIconType.GLOBAL;
        iconTypes[numberColumn - 1] = DragIconType.GLOBAL;
        return iconTypes;
    }

    public void randomGraph(RootLayout rootLayout) {
        this.rootLayout = rootLayout;
        int numberColumn = getNumberColumn();
        for (DragIconType type : randomType(numberColumn)) {
            int number;
            switch (type) {
                case NODE:
                    number = approximateNode / (numberColumn / 2);
                    break;
                case JOINT:
                    number = approximateJoint / (numberColumn / 2 - 1);
                    break;
                default:
                    number = approximateGlobal / 2;
                    break;
            }
            randomColumn(number, coordX, type);
            coordX += increment;
        }
        randomLink();
    }

    private int getNumberColumn() {
        int numberColumn = 2 * (approximateNode / (approximateGlobal / 2)) + 2;
        if (numberColumn % 2 == 0) {
            numberColumn--;
        }
        return numberColumn;
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
}
