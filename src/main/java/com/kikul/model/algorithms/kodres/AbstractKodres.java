package com.kikul.model.algorithms.kodres;

import com.kikul.model.editor.GraphNode;
import com.kikul.model.editor.RootGraph;
import com.kikul.model.editor.dragicon.DragIconType;

import java.util.*;

public abstract class AbstractKodres {

    private RootGraph graph;

    //    S(v) – max block area
    private int maxAreaBlock;

    //    T(v) – max number of terminals
    private int maxNumberTerminal;

    public RootGraph getGraph() {
        return graph;
    }

    public void setGraph(RootGraph graph) {
        this.graph = graph;
    }

    public void setRestrictionsByBlock(int maxAreaBlock, int maxNumberTerminal) {
        this.maxAreaBlock = maxAreaBlock;
        this.maxNumberTerminal = maxNumberTerminal;
    }

    private Map<GraphNode, Integer> getConNodes() {
        Map<GraphNode, Integer> conMap = new HashMap<>();
        for (GraphNode node : graph.getNodesByType(DragIconType.NODE)) {
            int numberLinks = 0;
            for (GraphNode linkNode : graph.getNodesLink(node))
                if (linkNode.getType().equals(DragIconType.GLOBAL) && checkId(linkNode))
                    ++numberLinks;
            conMap.put(node, numberLinks);
        }
        return conMap;
    }

    private boolean checkId(GraphNode node) {
        return graph.getNodeLinksById(node.getId()) != null;
    }

    private Map<GraphNode, Integer> getMinConNodes(Map<GraphNode, Integer> maxCon) {
        Map<GraphNode, Integer> conMap = new HashMap<>();
        int numberLink = Integer.MAX_VALUE;
        for (Map.Entry<GraphNode, Integer> entry : maxCon.entrySet()) {
            int count = 0;
            for (GraphNode linkNode : graph.getNodesLink(entry.getKey()))
                if (graph.getNodesLink(linkNode).size() > 1 && checkId(linkNode))
                    ++count;
            if (count < numberLink) {
                numberLink = count;
                conMap.clear();
                conMap.put(entry.getKey(), count);
            } else if (count == numberLink) {
                conMap.put(entry.getKey(), count);
            }
        }
        return conMap;
    }

    private Map<GraphNode, Integer> getMaxCons(Map<GraphNode, Integer> conMap) {
        int maxValue = Collections.max(conMap.values());
        Map<GraphNode, Integer> maxCon = new HashMap<>();
        for (Map.Entry<GraphNode, Integer> entry : conMap.entrySet())
            if (entry.getValue().equals(maxValue))
                maxCon.put(entry.getKey(), entry.getValue());
        return maxCon;
    }

    public GraphNode chooseFirstNode() {
        Map<GraphNode, Integer> conMap = getConNodes();
        Map<GraphNode, Integer> maxCon = getMaxCons(conMap);
        if (maxCon.size() == 1) {
            return maxCon.keySet().iterator().next();
        } else {
            conMap = getMinConNodes(maxCon);
            return getNodeWithMinDis(conMap);
        }
    }

    private Map<GraphNode, Integer> getConNodeWithBlock(List<GraphNode> blocksNodes) {
        Map<GraphNode, Integer> conMap = new HashMap<>();
        List<GraphNode> linkNodes = new ArrayList<>();
        for (GraphNode blocksNode : blocksNodes)
            for (GraphNode link : graph.getNodesLink(blocksNode))
                if (graph.getNodesLink(link).size() > 1 && !linkNodes.contains(link) && checkId(link))
                    linkNodes.add(link);

        for (GraphNode node : graph.getNodesByType(DragIconType.NODE))
            if (!blocksNodes.contains(node)) {
                int count = 0;
                for (GraphNode link : linkNodes)
                    if (node.getLinkedNodes().contains(link))
                        conMap.put(node, ++count);
            }
        if (conMap.size() == 0)
            for (GraphNode node : graph.getNodesByType(DragIconType.NODE))
                if (!blocksNodes.contains(node))
                    conMap.put(node, 0);
        return conMap;
    }

    private Map<GraphNode, Integer> getDisNodeWithBlock(List<GraphNode> blocksNodes, Map<GraphNode, Integer> maxCon) {
        Map<GraphNode, Integer> conMap = new HashMap<>();
        List<GraphNode> linkNodes = new ArrayList<>();
        for (GraphNode blocksNode : blocksNodes)
            for (GraphNode link : graph.getNodesLink(blocksNode))
                if (!linkNodes.contains(link) && checkId(link))
                    linkNodes.add(link);

        int numberLink = Integer.MAX_VALUE;
        for (Map.Entry<GraphNode, Integer> entry : maxCon.entrySet()) {
            int count = 0;
            for (GraphNode node : graph.getNodesLink(entry.getKey())) {
                if (linkNodes.contains(node)) {
                    count++;
                }
            }
            count = graph.getNodesLink(entry.getKey()).size() + linkNodes.size() - count;
            if (count < numberLink) {
                numberLink = count;
                conMap.clear();
                conMap.put(entry.getKey(), count);
            } else if (count == numberLink) {
                conMap.put(entry.getKey(), count);
            }
        }
        return conMap;
    }

    public GraphNode shiftToBlock(List<GraphNode> blocksNode) {
        Map<GraphNode, Integer> conMap = getConNodeWithBlock(blocksNode);
        Map<GraphNode, Integer> maxCon = getMaxCons(conMap);
        if (maxCon.size() == 1) {
            return maxCon.keySet().iterator().next();
        } else {
            conMap = getDisNodeWithBlock(blocksNode, maxCon);
            return getNodeWithMinDis(conMap);
        }
    }

    private GraphNode getNodeWithMinDis(Map<GraphNode, Integer> conMap) {
        if (conMap.size() == 1)
            return conMap.keySet().iterator().next();
        else
            return Collections.min(conMap.keySet(),
                    (key1, key2) -> key1.getNumNode() > key2.getNumNode() ? 1 : -1);
    }

    public boolean limitOfBlock(List<GraphNode> nodeToBlock, GraphNode node) {
        List<GraphNode> linkNodes = new ArrayList<>();
        List<GraphNode> nodes = new ArrayList<>(nodeToBlock);
        nodes.add(node);
        int numberLink = 0;
        for (GraphNode blocksNode : nodes) {
            for (GraphNode link : blocksNode.getLinkedNodes()) {
                if (!linkNodes.contains(link)) {
                    linkNodes.add(link);
                }
                ++numberLink;
            }
        }
        return linkNodes.size() <= maxNumberTerminal && numberLink <= maxAreaBlock;
    }

    public boolean lexicographicalVerification(List<GraphNode> nodesToBlock) {
        List<GraphNode> nodes = graph.getNodesByType(DragIconType.NODE);
        while (true) {
            if (nodes.size() != 0) {
                GraphNode minNode = Collections.min(nodes,
                        (key1, key2) -> key1.getNumNode() > key2.getNumNode() ? 1 : -1);
                if (limitOfBlock(nodesToBlock, minNode)) {
                    nodesToBlock.add(minNode);
                    removeBlock(nodesToBlock);
                    return true;
                }
                nodes.remove(minNode);
            } else {
                return false;
            }
        }
    }

    private synchronized void changeJointToGlobal(List<GraphNode> nodesToBlock, List<GraphNode> jointList) {
        for (GraphNode jointNode : jointList) {
            for (GraphNode node : graph.getNodes()) {
                if (node.equals(jointNode)) {
                    node.setType(DragIconType.GLOBAL);
                } else {
                    for (GraphNode linkNode : graph.getNodesLink(node))
                        if (linkNode.equals(jointNode) && checkId(linkNode))
                            linkNode.setType(DragIconType.GLOBAL);
                }
            }
            for (GraphNode node : nodesToBlock) {
                if (node.equals(jointNode))
                    node.setType(DragIconType.GLOBAL);
                else
                    for (GraphNode linkNode : node.getLinkedNodes())
                        if (linkNode.equals(jointNode))
                            linkNode.setType(DragIconType.GLOBAL);
            }
        }
    }

    public void removeBlock(List<GraphNode> nodesToBlock) {
        List<GraphNode> jointList = new ArrayList<>();
        for (GraphNode node : graph.getNodesByType(DragIconType.NODE)) {
            if (nodesToBlock.contains(node)) {
                for (GraphNode link : node.getLinkedNodes()) {
                    if (graph.getNodesLink(link).size() == 1) {
                        graph.getNodes().remove(link);
                    } else {
                        graph.getNodesLink(link).remove(node);
                        jointList.add(link);
                    }
                }
                graph.getNodes().remove(node);
            }
        }
        for (GraphNode node : graph.getNodes()) {
            node.getLinkedNodes().removeAll(nodesToBlock);
        }
        changeJointToGlobal(nodesToBlock, jointList);
    }

    public abstract List<List<GraphNode>> start(RootGraph graph, int maxAreaBlock, int maxNumberTerminal);
}