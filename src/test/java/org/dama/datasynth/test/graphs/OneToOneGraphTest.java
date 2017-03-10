package org.dama.datasynth.test.graphs;

import org.dama.datasynth.test.graphs.types.Edge;
import org.dama.datasynth.test.graphs.types.Graph;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by aprat on 27/02/17.
 */
public class OneToOneGraphTest {

    @Test
    public void testGraph() {
        long numNodes = 100000;
        Graph graph = new OneToOneGraph(numNodes);
        for(Long node : graph.getNodes()) {
            Set<Long> neighbors = graph.getNeighbors(node);

            if (node < numNodes / 2l) {
                for (Long neighbor : neighbors) {
                    assertTrue(neighbor == node + numNodes / 2);
                }
            } else {
                for (Long neighbor : neighbors) {
                    assertTrue(neighbor == node - numNodes / 2);
                }
            }
        }

    }

}
