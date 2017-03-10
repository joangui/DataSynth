package org.dama.datasynth.matching.graphs.types;

import org.dama.datasynth.matching.graphs.types.Graph;
import org.dama.datasynth.matching.graphs.types.Partition;

/**
 * Created by aprat on 27/02/17.
 */
public interface GraphPartitioner {
    public void initialize(Graph graph, Class<? extends Traversal> traversal, double [] partitionCapacities);
    public Partition getPartition();
}
