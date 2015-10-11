package com.kikul.model.algorithms.kodres;

import com.kikul.model.editor.dragicon.DragIconType;
import com.kikul.model.editor.GraphNode;
import com.kikul.model.editor.RootGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DecompositionKodres extends AbstractKodres {

    @Override
    public List<List<GraphNode>> start(RootGraph graph, int maxAreaBlock, int maxNumberTerminal) {
        setGraph(graph);
        setRestrictionsByBlock(maxAreaBlock, maxNumberTerminal);

        List<Thread> threads = new ArrayList<>(2);
        List<List<GraphNode>> nodeLists = Arrays.asList(new ArrayList<>(), new ArrayList<>());
        nodeLists.get(0).add(chooseFirstNode());

        formFirstBlock(graph, nodeLists);
        startThread(maxAreaBlock, maxNumberTerminal, threads, nodeLists, 0);
        formSecondBlock(graph, nodeLists);
        startThread(maxAreaBlock, maxNumberTerminal, threads, nodeLists, 1);
        waitThreads(threads);
        return nodeLists;
    }

    private void formSecondBlock(RootGraph graph, List<List<GraphNode>> lists) {
        for (GraphNode graphNode : graph.getNodesByType(DragIconType.NODE)) {
            if (!lists.get(0).contains(graphNode)) {
                lists.get(1).add(graphNode);
            }
        }
    }

    private void formFirstBlock(RootGraph graph, List<List<GraphNode>> lists) {
        for (int i = 0; i < graph.getNodesByType(DragIconType.NODE).size() / 2; i++) {
            lists.get(0).add(shiftToBlock(lists.get(0)));
        }
    }

    private void startThread(int maxAreaBlock, int maxNumberTerminal, List<Thread> threads, List<List<GraphNode>> lists, int number) {
        threads.add(new Thread(new ThreadKodres(lists.get(number), maxAreaBlock, maxNumberTerminal)));
        threads.get(number).start();
    }

    private void waitThreads(List<Thread> threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
