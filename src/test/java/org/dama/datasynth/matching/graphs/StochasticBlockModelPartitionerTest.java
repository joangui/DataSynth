package org.dama.datasynth.matching.graphs;

import org.dama.datasynth.matching.StochasticBlockModel;
import org.dama.datasynth.matching.graphs.types.Graph;
import org.dama.datasynth.matching.graphs.types.GraphPartitioner;
import org.dama.datasynth.matching.graphs.types.Partition;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by aprat on 27/02/17.
 */
public class StochasticBlockModelPartitionerTest {

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
	    long blockSize = 10;
	    Map<String,Integer> mapping = new HashMap<>();
	    mapping.put("A",0);
		mapping.put("B",1);
		long sizes[] = {blockSize,blockSize};
		double [][] probabilities = new double[2][2];
		probabilities[0][0] = 0.7;
		probabilities[0][1] = 0.1;
		probabilities[1][0] = 0.1;
		probabilities[1][1] = 0.7;
		StochasticBlockModel<String> blockModel = new StochasticBlockModel(mapping,sizes,probabilities);
		Graph stochasticGraph = new StochasticBlockModelGraph (blockModel);
		GraphPartitioner partitioner = new StochasticBlockModelPartitioner(stochasticGraph,BFSTraversal.class, blockModel);

		Partition partition = partitioner.getPartition();
        for (Map.Entry<Integer,Set<Long>> entry : partition.entrySet()) {
            assertTrue(entry.getValue().size() == (blockSize));
        }
	}
}
