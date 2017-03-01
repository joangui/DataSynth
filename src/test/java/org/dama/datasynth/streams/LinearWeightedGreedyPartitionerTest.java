package org.dama.datasynth.streams;

import org.junit.Test;

/**
 * Created by aprat on 27/02/17.
 */
public class LinearWeightedGreedyPartitionerTest {

    @Test
    void testPartitioner(){
        long numNodes = 1000000;
        long numPartitions = 16;
        OneToOneGraph oneToOneGraph = new OneToOneGraph(numNodes);
        LinearWeightedGreedyPartitioner partitioner = new LinearWeightedGreedyPartitioner();
        partitioner.initialize(numNodes, numPartitions);
        int partitions[] = new int[(int)numPartitions];
        for(int i = 0; i < numPartitions; ++i) {
            partitions[i] = 0;
        }

        while(oneToOneGraph.hasNext()) {
            Edge edge = oneToOneGraph.nextEdge();
            Edge nodePartitions = partitioner.partition(edge);
            if(nodePartitions.tail != -1) {
                partitions[(int) nodePartitions.tail]++;
            }

            if(nodePartitions.head != -1) {
                partitions[(int) nodePartitions.head]++;
            }
        }

        for(int i = 0; i < numPartitions; ++i) {
            System.out.println(partitions[i]);
        }
    }
}
