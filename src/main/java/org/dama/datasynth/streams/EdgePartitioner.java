package org.dama.datasynth.streams;

/**
 * Created by aprat on 27/02/17.
 */
public interface EdgePartitioner {
    public void initialize(long numNodes, long numPartitions);
    public Edge partition(Edge edge);
}
