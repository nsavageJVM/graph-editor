package com.kikul.model.editor;

import com.kikul.model.editor.dragicon.DragIconType;
import com.kikul.model.editor.node.DraggableNode;
import javafx.geometry.Point2D;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlType(propOrder = {"id", "numNode", "type", "x", "y", "linkedNodes"})
public class GraphNode {

    private double x;

    private double y;

    private String id;

    private int numNode;

    private DragIconType type;

    @XmlTransient
    private DraggableNode node;

    @XmlElementWrapper(name = "linkedNodes")
    @XmlElement(name = "NODE", type = GraphNode.class)
    private List<GraphNode> linkedNodes;

    public GraphNode() {
        this.linkedNodes = new ArrayList<>();
    }

    public GraphNode(DraggableNode node) {
        this();
        this.id = node.getId();
        this.numNode = node.getNumberDraggableNode();
        this.type = node.getType();
        this.node = node;
    }

    public int getNumNode() {
        return numNode;
    }

    public void setNumNode(int numNode) {
        this.numNode = numNode;
    }

    public DragIconType getType() {
        return type;
    }

    public void setType(DragIconType type) {
        this.type = type;
    }

    public double getX() {
        return node.getPointNode().getX();
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return node.getPointNode().getY();
    }

    public void setY(double y) {
        this.y = y;
    }

    public Point2D getCursorPoint() {
        return new Point2D(x, y);
    }

    public List<GraphNode> getLinkedNodes() {
        return linkedNodes;
    }

    public void addLinkedNodes(GraphNode node) {
        this.linkedNodes.add(node);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        GraphNode node = (GraphNode) obj;
        if (Double.compare(node.x, x) != 0) return false;
        if (Double.compare(node.y, y) != 0) return false;
        if (numNode != node.numNode) return false;
        if (!id.equals(node.id)) return false;
        if (type != node.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id.hashCode();
        result = 31 * result + numNode;
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + type.hashCode();
        return result;
    }

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNode(DraggableNode node) {
        this.node = node;
    }

    public String toString() {
        return "GraphNode{" + type + "_" + numNode + "}";
    }

    public DraggableNode getDraggableNode() {
        return node;
    }
}
