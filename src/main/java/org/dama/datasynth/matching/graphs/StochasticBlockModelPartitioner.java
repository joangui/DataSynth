package org.dama.datasynth.matching.graphs;

import org.dama.datasynth.matching.graphs.types.Graph;
import org.dama.datasynth.matching.graphs.types.GraphPartitioner;
import org.dama.datasynth.matching.graphs.types.Partition;
import org.dama.datasynth.matching.graphs.types.Traversal;

/**
 * Created by aprat on 10/03/17.
 */
public class StochasticBlockModelPartitioner extends GraphPartitioner {

    public StochasticBlockModelPartitioner(Graph graph, Class<? extends Traversal> traversalType) {
        super(graph, traversalType);
    }

    @Override
    public Partition getPartition() {
        return null;
    }
}
