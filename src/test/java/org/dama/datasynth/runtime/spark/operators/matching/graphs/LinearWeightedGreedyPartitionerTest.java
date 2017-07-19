package org.dama.datasynth.runtime.spark.operators.matching.graphs;

import org.dama.datasynth.runtime.spark.operators.matching.StochasticBlockModel;
import org.dama.datasynth.runtime.spark.operators.matching.graphs.types.Graph;
import org.dama.datasynth.runtime.spark.operators.matching.graphs.types.Partition;
import org.junit.Test;

import java.util.HashMap;
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
	    Map<String,Integer> mapping = new HashMap<>();
	    mapping.put("A",0);
		mapping.put("A",1);
		long sizes[] = {5,5};
		double [][] probabilities = new double[2][2];
		probabilities[0][0] = 0.7;
		probabilities[0][1] = 0.1;
		probabilities[1][0] = 0.1;
		probabilities[1][1] = 0.7;
		Graph stochasticGraph = new StochasticBlockModelGraph (new StochasticBlockModel(mapping,sizes,probabilities));
		double [] partitions = {0.5D, 0.5D};
		LinearWeightedGreedyPartitioner partitioner = new LinearWeightedGreedyPartitioner(stochasticGraph,BFSTraversal.class, partitions);

		Partition partition = partitioner.getPartition();
        for (Map.Entry<Integer,Set<Long>> entry : partition.entrySet()) {
            assertTrue(entry.getValue().size() == (blockSize));
        }
	}
}
