package com.kikul.model.algorithms.kodres;

import com.kikul.model.editor.GraphNode;
import com.kikul.model.editor.RootGraph;
import com.kikul.model.editor.dragicon.DragIconType;

import java.util.ArrayList;
import java.util.List;

public class SimpleKodres extends AbstractKodres {

    public SimpleKodres() {
    }

    @Override
    public List<List<GraphNode>> start(RootGraph graph, int maxAreaBlock, int maxNumberTerminal) {
        setGraph(graph);
        List<List<GraphNode>> listBlocks = new ArrayList<>();
        while (graph.getNodesByType(DragIconType.NODE).size() != 0) {
            List<GraphNode> nodesToBlock = new ArrayList<>();
            nodesToBlock.add(chooseFirstNode());

            setRestrictionsByBlock(maxAreaBlock, maxNumberTerminal);
            formBlock(graph, nodesToBlock);
            checkSize(graph, nodesToBlock);
            listBlocks.add(nodesToBlock);
        }
        return listBlocks;
    }

    private void checkSize(RootGraph graph, List<GraphNode> nodesToBlock) {
        if (graph.getSizeGraphNodes() == nodesToBlock.size()) {
            removeBlock(nodesToBlock);
        }
    }

    private void formBlock(RootGraph graph, List<GraphNode> nodesToBlock) {
        while (graph.getSizeGraphNodes() != nodesToBlock.size()) {
            GraphNode node = shiftToBlock(nodesToBlock);
            if (limitOfBlock(nodesToBlock, node)) {
                nodesToBlock.add(node);
            } else {
                removeBlock(nodesToBlock);
                lexicographicalVerification(nodesToBlock);
                break;
            }
        }
    }
}
