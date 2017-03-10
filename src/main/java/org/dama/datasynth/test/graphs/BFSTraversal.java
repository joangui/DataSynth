package org.dama.datasynth.test.graphs;

import org.dama.datasynth.test.graphs.types.Graph;
import org.dama.datasynth.test.graphs.types.Traversal;

import java.util.*;

/**
 * Created by aprat on 9/03/17.
 */
public class BFSTraversal implements Traversal {

    private LinkedList<Long> sortedNodes = new LinkedList<Long>();

    @Override
    public void initialize(Graph graph) {
        LinkedList<Long> nodes = new LinkedList<>(graph.getNodes());
        Collections.shuffle(nodes);
        LinkedList<Long> bfsQueue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();
        for(Long node : nodes) {
            if(!visited.contains(node)) {
                bfsQueue.add(node);
                visited.add(bfsQueue.getFirst());
                while (!bfsQueue.isEmpty()) {
                    long next = bfsQueue.pollFirst();
                    sortedNodes.add(next);
                    for (Long neighbor : graph.getNeighbors(next)) {
                        if (!visited.contains(neighbor)) {
                            bfsQueue.push(neighbor);
                            visited.add(neighbor);
                        }
                    }
                }
            }
        }

    }

    @Override
    public boolean hasNext() {
        return !sortedNodes.isEmpty();
    }

    @Override
    public Long next() {
        return sortedNodes.pollFirst();
    }
}