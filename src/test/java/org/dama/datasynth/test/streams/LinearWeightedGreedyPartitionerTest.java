package org.dama.datasynth.test.streams;

import org.dama.datasynth.test.graphreader.types.Edge;
import org.dama.datasynth.test.graphreader.types.EdgePartitions;
import static org.junit.Assert.assertTrue;

import org.dama.datasynth.test.graphreader.types.Partition;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

/**
 * Created by aprat on 27/02/17.
 */
public class LinearWeightedGreedyPartitionerTest {

	@Test
	public void testPartitioner() {
		long numNodes = 12;
		OneToOneGraph oneToOneGraph = new OneToOneGraph(numNodes);
		LinearWeightedGreedyPartitioner partitioner = new LinearWeightedGreedyPartitioner();
		double [] partitions = {0.5D, 0.5D};
		partitioner.initialize(oneToOneGraph, partitions);

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
}
