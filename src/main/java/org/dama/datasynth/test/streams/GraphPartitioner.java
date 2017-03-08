package org.dama.datasynth.test.streams;

import org.dama.datasynth.test.graphreader.types.Graph;
import org.dama.datasynth.test.graphreader.types.Partition;

/**
 * Created by aprat on 27/02/17.
 */
public interface GraphPartitioner {
    public void initialize(Graph graph, double [] partitionCapacities);
    public Partition getPartition();
}
