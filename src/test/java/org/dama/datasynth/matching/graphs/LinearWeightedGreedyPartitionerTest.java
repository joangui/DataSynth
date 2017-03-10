package org.dama.datasynth.matching.graphs;

import org.dama.datasynth.matching.graphs.types.Graph;
import org.dama.datasynth.matching.graphs.types.Partition;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by aprat on 27/02/17.
 */
public class LinearWeightedGreedyPartitionerTest {

	@Test
	public void oneToOneGraph() {
		long numNodes = 12;
		OneToOneGraph oneToOneGraph = new OneToOneGraph(numNodes);
		double [] partitions = {0.5D, 0.5D};
		LinearWeightedGreedyPartitioner partitioner = new LinearWeightedGreedyPartitioner(oneToOneGraph, BFSTraversal.class, partitions);

		Partition partition = partitioner.getPartition();
		for (Map.Entry<Integer,Set<Long>> entry : partition.entrySet()) {
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
		double [] partitions = {0.5D, 0.5D};
		LinearWeightedGreedyPartitioner partitioner = new LinearWeightedGreedyPartitioner(stochasticGraph,BFSTraversal.class, partitions);

		Partition partition = partitioner.getPartition();
        for (Map.Entry<Integer,Set<Long>> entry : partition.entrySet()) {
            assertTrue(entry.getValue().size() == (blockSize));
        }
	}
}
