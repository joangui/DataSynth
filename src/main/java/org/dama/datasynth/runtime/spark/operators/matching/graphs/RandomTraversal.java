package org.dama.datasynth.runtime.spark.operators.matching.graphs;

import org.dama.datasynth.runtime.spark.operators.matching.graphs.types.Graph;
import org.dama.datasynth.runtime.spark.operators.matching.graphs.types.Traversal;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by aprat on 12/03/17.
 */
public class RandomTraversal implements Traversal {

    private LinkedList<Long> nodes = null;
    @Override
    public void initialize(Graph graph) {
        nodes = new LinkedList<>(graph.getNodes());
        Random random = new Random(0L);
        Collections.shuffle(nodes,random);
    }

    @Override
    public boolean hasNext() {
        return !nodes.isEmpty();
    }

    @Override
    public Long next() {
        return nodes.pollFirst();
    }
}
