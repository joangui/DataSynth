package org.dama.datasynth.matching.graphs;

import org.dama.datasynth.matching.graphs.types.GraphPartitioner;
import org.dama.datasynth.matching.graphs.types.Graph;
import org.dama.datasynth.matching.graphs.types.Partition;
import org.dama.datasynth.matching.graphs.types.Traversal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by aprat on 27/02/17.
 */
public class LinearWeightedGreedyPartitioner implements GraphPartitioner {

    private Graph partialGraph = new Graph();
	private Graph graph = null;
	private int partitionCapacities []= null;
	private int partitionCounts []= null;
	private HashMap<Long,Integer> vertexToPartition = new HashMap<>();


    private double score(int partitionNeighbors, long partitionCount, int partitionCapacity) {
        return partitionNeighbors * (1 - (double) partitionCount / (double) partitionCapacity);
    }

	@Override
	public void initialize(Graph graph, Class<? extends Traversal> traversalType, double [] partitionCapacities) {
		this.graph = graph;

		this.partitionCapacities = new int[partitionCapacities.length];
		Arrays.setAll(this.partitionCapacities, (int i ) -> (int)(partitionCapacities[i]*graph.getNumNodes()));
		this.partitionCounts = new int[partitionCapacities.length];
		Arrays.fill(partitionCounts,0);

		Traversal traversal = null;
		try {
			traversal = traversalType.newInstance();
			traversal.initialize(graph);
		} catch ( Exception e) {
			throw new RuntimeException(e);
		}

		while(traversal.hasNext()) {
		    long node = traversal.next();
		    int partition = findBestPartition(node);
		    vertexToPartition.put(node,partition);
		    partitionCounts[partition]+=1;
        }
	}


	private int findBestPartition(long node) {
        int partitionNeighbors[] = new int[partitionCounts.length];
        Arrays.fill(partitionNeighbors,0);

		Set<Long> neighbors = graph.getNeighbors(node);
		for (Long neighbor : neighbors) {
			Integer i = vertexToPartition.get(neighbor);
			if (i != null) {
				partitionNeighbors[i]++;
			}
		}
		int bestPartition = 0;
		double bestScore = score(partitionNeighbors[0], partitionCounts[0],partitionCapacities[0]);
		for (int i = 1; i < partitionCounts.length; ++i) {
			double newScore = score(partitionNeighbors[i], partitionCounts[i], partitionCapacities[i]);
			if (newScore > bestScore) {
				bestPartition = i;
				bestScore = newScore;
			}
		}

		if (bestScore == 0) {
			int leastPopulatedPartition = 0;
			long minPupulation = partitionCounts[0];
			for (int i = 1; i < partitionCounts.length; i++) {
				long population = partitionCounts[i];
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
        Partition partition =  new Partition();
        for(Map.Entry<Long,Integer> entry : vertexToPartition.entrySet()){
            partition.addToPartition(entry.getKey(),(long)(entry.getValue()));
        }
        return partition;
    }
}
