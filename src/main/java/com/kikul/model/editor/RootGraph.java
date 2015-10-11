package com.kikul.model.editor;

import com.kikul.model.editor.dragicon.DragIconType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "graph")
public class RootGraph {

    @XmlElements({
            @XmlElement(name = "NODE", type = GraphNode.class)
    })
    private List<GraphNode> nodes;

    public RootGraph() {
        this.nodes = new ArrayList<>();
    }

    public RootGraph(List<GraphNode> nodes) {
        this.nodes = nodes;
    }

    public void addNode(GraphNode node) {
        nodes.add(node);
    }

    public List<GraphNode> getNodes() {
        return nodes;
    }

    public int getOrder() {
        return nodes.size();
    }

    public int getSizeGraphNodes() {
        return getNodesByType(DragIconType.NODE).size();
    }

    public List<GraphNode> getNodesByType(DragIconType type) {

        List<GraphNode> nodesByType = new ArrayList<>();
        for (GraphNode node : nodes) {
            if (node.getType().equals(type)) {
                nodesByType.add(node);
            }
        }

        return nodesByType;
    }

    public List<GraphNode> getNodesLink(GraphNode node) {
        int index = nodes.indexOf(node);
        return nodes.get(index).getLinkedNodes();
    }

    public GraphNode getNodeLinksById(String id) {
        for (GraphNode graphNode : nodes) {
            if (graphNode.getId().equals(id)) {
                return graphNode;
            }
        }
        return null;
    }

    public String toString() {
        return "RootGraph{" +
                "nodes=" + nodes +
                '}';
    }

    public void clear() {
        nodes = new ArrayList<>();
    }

    public boolean isEmpty() {
        return getOrder() == 0;
    }
}