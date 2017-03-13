package org.dama.datasynth.matching.graphs;

import org.dama.datasynth.matching.graphs.types.Graph;
import org.dama.datasynth.matching.graphs.types.GraphPartitioner;
import org.dama.datasynth.matching.graphs.types.Partition;
import org.dama.datasynth.matching.graphs.types.Traversal;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by aprat on 27/02/17.
 */
public class LinearWeightedGreedyPartitioner extends GraphPartitioner {

	private int partitionCapacities []= null;
	private Partition partition = new Partition();
	private int numPartitions = 0;

    private double score(int partitionNeighbors, long partitionCount, int partitionCapacity) {
        return partitionNeighbors * (1 - (double) partitionCount / (double) partitionCapacity);
    }

	public LinearWeightedGreedyPartitioner(Graph graph, Class<? extends Traversal> traversalType, double [] partitionCapacities) {
		super(graph, traversalType);

		this.numPartitions = partitionCapacities.length;
		this.partitionCapacities = new int[numPartitions];
		Arrays.setAll(this.partitionCapacities, (int i ) -> (int)(partitionCapacities[i]*graph.getNumNodes()));

		while(traversal.hasNext()) {
		    long node = traversal.next();
		    int partitionId = findBestPartition(node);
		    partition.addToPartition(node,partitionId);
        }
	}


	private int findBestPartition(long node) {
        int partitionNeighbors[] = new int[numPartitions];
        Arrays.fill(partitionNeighbors,0);

		Set<Long> neighbors = graph.getNeighbors(node);
		for (Long neighbor : neighbors) {
			Integer i = partition.getNodePartition(neighbor);
			if (i != null) {
				partitionNeighbors[i]++;
			}
		}
		int bestPartition = 0;
		double bestScore = score(partitionNeighbors[0], partition.getPartitionSize(0),partitionCapacities[0]);
		for (int i = 1; i < numPartitions; ++i) {
			double newScore = score(partitionNeighbors[i], partition.getPartitionSize(0), partitionCapacities[i]);
			if (newScore > bestScore) {
				bestPartition = i;
				bestScore = newScore;
			}
		}

		if (bestScore == 0) {
			int leastPopulatedPartition = 0;
			long minPupulation = partition.getPartitionSize(0);
			for (int i = 1; i < numPartitions; i++) {
				long population = partition.getPartitionSize(i);
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
    public Partition getPartition() {
        return partition;
    }
}
