package org.dama.datasynth.matching.graphs;

import org.dama.datasynth.matching.StochasticBlockModel;
import org.dama.datasynth.matching.graphs.types.Graph;
import org.dama.datasynth.matching.graphs.types.GraphPartitioner;
import org.dama.datasynth.matching.graphs.types.Partition;
import org.dama.datasynth.matching.graphs.types.Traversal;

/**
 * Created by aprat on 10/03/17.
 */
public class StochasticBlockModelPartitioner extends GraphPartitioner {

    private StochasticBlockModel blockModel = null;
    private Partition partition = new Partition();

    public StochasticBlockModelPartitioner(Graph graph, Class<? extends Traversal> traversalType, StochasticBlockModel blockModel) {
        super(graph, traversalType);
        this.blockModel = blockModel;

    }


    private int findBestPartition( long node) {
        return 1;
    }

    @Override
    public Partition getPartition() {
        return null;
    }
}
