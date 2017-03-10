package org.dama.datasynth.matching.graphs.types;

/**
 * Created by aprat on 27/02/17.
 */
public interface GraphPartitioner {
    public void initialize(Graph graph, Class<? extends Traversal> traversal, double [] partitionCapacities);
    public Partition getPartition();
}
