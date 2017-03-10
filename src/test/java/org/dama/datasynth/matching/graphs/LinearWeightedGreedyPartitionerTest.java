package org.dama.datasynth.matching.graphs;

import static org.junit.Assert.assertTrue;

import org.dama.datasynth.matching.graphs.types.Graph;
import org.dama.datasynth.matching.graphs.types.Partition;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

/**
 * Created by aprat on 27/02/17.
 */
public class LinearWeightedGreedyPartitionerTest {

	@Test
	public void oneToOneGraph() {
		long numNodes = 12;
		OneToOneGraph oneToOneGraph = new OneToOneGraph(numNodes);
		LinearWeightedGreedyPartitioner partitioner = new LinearWeightedGreedyPartitioner();
		double [] partitions = {0.5D, 0.5D};
		partitioner.initialize(oneToOneGraph, BFSTraversal.class, partitions);

		Partition partition = partitioner.getPartition();
		for (Map.Entry<Long,Set<Long>> entry : partition.entrySet()) {
			assertTrue(entry.getValue().size() == (numNodes / 2));
			for(Long l : entry.getValue()) {
				if(l < numNodes/2) {
					assertTrue(entry.getValue().contains(l+numNodes/2));
		 		}
			}
		}
	}

	@Test
	public void stochasticGraph() {
	    long blockSize = 5;
		Graph stochasticGraph = new StochasticBlockModelGraph (blockSize,1.0,0.1);
		LinearWeightedGreedyPartitioner partitioner = new LinearWeightedGreedyPartitioner();
		double [] partitions = {0.5D, 0.5D};
		partitioner.initialize(stochasticGraph,BFSTraversal.class, partitions);

		Partition partition = partitioner.getPartition();
        for (Map.Entry<Long,Set<Long>> entry : partition.entrySet()) {
            assertTrue(entry.getValue().size() == (blockSize));
        }
	}
}
