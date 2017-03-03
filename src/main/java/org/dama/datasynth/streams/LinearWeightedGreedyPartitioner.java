package org.dama.datasynth.streams;

import org.dama.datasynth.graphreader.types.Edge;
import org.dama.datasynth.graphreader.types.EdgePartitions;
import org.dama.datasynth.graphreader.types.Graph;
import java.util.Set;

/**
 * Created by aprat on 27/02/17.
 */
public class LinearWeightedGreedyPartitioner implements EdgePartitioner {

	private long numNodes = 0L;
	private long numPartitions = 0L;
	private int vertexPartition[] = null;
	private long partitionCapacity = 0L;
	private long partitionSize[] = null;
	private Graph graph = null;

	int partitionCounts[] = null;

	@Override
	public void initialize(long numNodes, int numPartitions) {
		this.numNodes = numNodes;
		this.numPartitions = numPartitions;
		this.vertexPartition = new int[(int) numNodes];
		for (int i = 0; i < numNodes; ++i) {
			vertexPartition[i] = -1;
		}
		this.graph = new Graph(numNodes);
		this.partitionCounts = new int[(int) numPartitions];
		this.partitionSize = new long[(int) numPartitions];
		for (int i = 0; i < numPartitions; ++i) {
			partitionSize[i] = 0L;
		}
		this.partitionCapacity = numNodes / numPartitions;
	}

	private double score(int partitionCount, long partitionSize) {
		return partitionCount * (1 - (double) partitionSize / (double) partitionCapacity);
	}

	private int findBestPartition(long node) {
		for (int i = 0; i < numPartitions; ++i) {
			partitionCounts[i] = 0;
		}
		Set<Long> neighbors = graph.neighbors(node);
		for (Long neighbor : neighbors) {
			int i = vertexPartition[(int) ((long) neighbor)];
			if (i >= 0) {
				partitionCounts[i]++;
			}
		}
		int bestPartition = 0;
		double bestScore = score(partitionCounts[0], partitionSize[0]);
		for (int i = 1; i < numPartitions; ++i) {
			double newScore = score(partitionCounts[i], partitionSize[i]);
			if (score(partitionCounts[i], partitionSize[i]) > bestScore) {
				bestPartition = i;
				bestScore = newScore;
			}
		}

		if (bestScore == 0) {
			int leastPopulatedPartition = 0;
			long minPupulation = partitionSize[0];
			for (int i = 1; i < numPartitions; i++) {
				long population = partitionSize[i];
				if (population < minPupulation) {
					minPupulation = population;
					leastPopulatedPartition = i;
				}
			}
			return leastPopulatedPartition;
		}

		return bestPartition;
	}

	@Override
	public EdgePartitions partition(Edge edge) {

		graph.addEdge(edge.tail, edge.head);

		EdgePartitions returnEdge = new EdgePartitions();
		returnEdge.tailPartition = vertexPartition[(int) edge.tail];
		if (returnEdge.tailPartition == -1) {
			returnEdge.tailPartition = findBestPartition(edge.tail);
			vertexPartition[(int) edge.tail] = (int) returnEdge.tailPartition;
			partitionSize[(int) returnEdge.tailPartition]++;
		}

		returnEdge.headPartitions = vertexPartition[(int) edge.head];
		if (returnEdge.headPartitions == -1) {
			returnEdge.headPartitions = findBestPartition(edge.head);
			vertexPartition[(int) edge.head] = (int) returnEdge.headPartitions;
			partitionSize[(int) returnEdge.headPartitions]++;
		}
		return returnEdge;
	}

}
