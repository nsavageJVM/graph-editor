package com.kikul.model.algorithms.kodres;

import com.kikul.model.editor.NodeContainer;
import com.kikul.model.editor.GraphNode;
import com.kikul.model.editor.RootGraph;
import com.kikul.model.editor.dragicon.DragIconType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThreadKodres implements Runnable {

    private int area;
    private int terminals;
    private List<GraphNode> listOneBlock = new ArrayList<>();

    public ThreadKodres(List<GraphNode> listOneBlock, int area, int terminals) {
        this.listOneBlock = listOneBlock;
        this.area = area;
        this.terminals = terminals;
    }

    @Override
    public void run() {
        Set<GraphNode> nodesNewGraph = makeSetGraphNodes(listOneBlock);
        RootGraph newGraph = changeJointToGlobal(nodesNewGraph);
        new SimpleKodres().start(newGraph, area, terminals);
    }

    private RootGraph changeJointToGlobal(Set<GraphNode> nodesNewGraph) {
        for (GraphNode nodes : nodesNewGraph) {
            if (nodes.getType() == DragIconType.JOINT) {
                for (GraphNode linkJointNode : nodes.getLinkedNodes())
                    if (!nodesNewGraph.contains(linkJointNode)) {
                        changeTypeLinkNodes(nodes);
                        nodes.setType(DragIconType.GLOBAL);
                    }
            }
        }
        return new RootGraph(new ArrayList<>(nodesNewGraph));
    }

    private void changeTypeLinkNodes(GraphNode nodes) {
        for (GraphNode node : NodeContainer.graph.getNodesLink(nodes)) {
            int index = NodeContainer.graph.getNodesLink(node).indexOf(nodes);
            if (index > -1) {
                NodeContainer.graph.getNodesLink(node).get(index).setType(DragIconType.GLOBAL);
            }
        }
    }

    private Set<GraphNode> makeSetGraphNodes(List<GraphNode> listOneBlock) {
        Set<GraphNode> nodesNewGraph = new HashSet<>();
        for (GraphNode graphNode : listOneBlock) {
            GraphNode node = NodeContainer.graph.getNodeLinksById(graphNode.getId());
            nodesNewGraph.add(node);
            for (GraphNode link : node.getLinkedNodes())
                nodesNewGraph.add(NodeContainer.graph.getNodeLinksById(link.getId()));
        }
        return nodesNewGraph;
    }
}