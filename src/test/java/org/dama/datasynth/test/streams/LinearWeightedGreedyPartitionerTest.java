package org.dama.datasynth.test.streams;

import org.dama.datasynth.test.graphreader.types.Edge;
import org.dama.datasynth.test.graphreader.types.EdgePartitions;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Created by aprat on 27/02/17.
 */
public class LinearWeightedGreedyPartitionerTest {

	@Test
	public void testPartitioner() {
		long numNodes = 1000000;
		int numPartitions = 16;
		OneToOneGraph oneToOneGraph = new OneToOneGraph(numNodes);
		LinearWeightedGreedyPartitioner partitioner = new LinearWeightedGreedyPartitioner();
		partitioner.initialize(numNodes, numPartitions);
		int partitions[] = new int[(int) numPartitions];
		for (int i = 0; i < numPartitions; ++i) {
			partitions[i] = 0;
		}

		while (oneToOneGraph.hasNext()) {
			Edge edge = oneToOneGraph.nextEdge();
			EdgePartitions nodePartitions = partitioner.partition(edge);
			if (nodePartitions.tailPartition != -1) {
				partitions[(int) nodePartitions.tailPartition]++;
			}

			if (nodePartitions.headPartitions != -1) {
				partitions[(int) nodePartitions.headPartitions]++;
			}
		}

		long recountedNodes = 0;
		for (int i = 0; i < numPartitions; ++i) {

			assertTrue(partitions[i] == (numNodes / numPartitions));
			recountedNodes += partitions[i];
		}
		assertTrue(recountedNodes == numNodes);
	}
}
