package org.dama.datasynth.test.streams;

import org.dama.datasynth.test.graphreader.types.Edge;
import org.dama.datasynth.test.graphreader.types.EdgePartitions;

/**
 * Created by aprat on 27/02/17.
 */
public interface EdgePartitioner {
    public void initialize(long numNodes, int numPartitions);
    public EdgePartitions partition(Edge edge);
}
