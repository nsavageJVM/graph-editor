package com.kikul.model.algorithms.dfs;

import com.kikul.model.editor.GraphNode;
import com.kikul.model.editor.RootGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class DFS {

    private static final Logger LOGGER = LoggerFactory.getLogger(DFS.class);

    private RootGraph graph;
    private Map<GraphNode, Mark> visitedMap = new LinkedHashMap<>();
    private int counter = 1;

    public DFS(RootGraph graph) {
        this.graph = graph;
    }

    private void dfs(GraphNode node) {
        if (visitedMap.containsKey(node)) return;

        visitedMap.put(node, new Mark(counter++, -1));
        for (GraphNode link : graph.getNodesLink(node)) {
            if (visitedMap.containsKey(link))
                continue;
            dfs(link);
        }
        Mark mark = visitedMap.get(node);
        mark.post = counter++;
    }

    public Map<GraphNode, Mark> start() {
        visitedMap.clear();
        graph.getNodes().forEach(this::dfs);

        LOGGER.info("DFS");
        for (Map.Entry<GraphNode, Mark> entry : visitedMap.entrySet()) {
            Mark m = entry.getValue();
            LOGGER.info(String.format("%1$s : (%2$d, %3$d)\n", entry.getKey(), m.pre, m.post));
        }
        return visitedMap;
    }

    static class Mark {

        public int pre;
        public int post;

        public Mark(int pre, int post) {
            this.pre = pre;
            this.post = post;
        }
    }
}
