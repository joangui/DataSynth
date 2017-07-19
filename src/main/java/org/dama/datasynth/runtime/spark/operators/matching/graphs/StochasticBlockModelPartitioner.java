package org.dama.datasynth.runtime.spark.operators.matching.graphs;

import org.dama.datasynth.runtime.spark.operators.matching.StochasticBlockModel;
import org.dama.datasynth.runtime.spark.operators.matching.graphs.types.Graph;
import org.dama.datasynth.runtime.spark.operators.matching.graphs.types.GraphPartitioner;
import org.dama.datasynth.runtime.spark.operators.matching.graphs.types.Partition;
import org.dama.datasynth.runtime.spark.operators.matching.graphs.types.Traversal;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by aprat on 10/03/17.
 */
public class StochasticBlockModelPartitioner extends GraphPartitioner {

    private StochasticBlockModel blockModel = null;
    private Partition partition = new Partition();
    private long[][] edgesOriginal =  null;
    private long[][] edgesCurrent = null;
    private long currentScore = 0L;

    public StochasticBlockModelPartitioner(Graph graph, Class<? extends Traversal> traversalType, StochasticBlockModel blockModel) {
        super(graph, traversalType);
        this.blockModel = blockModel;
        this.edgesOriginal = new long[blockModel.getNumBlocks()][blockModel.getNumBlocks()];
        this.edgesCurrent = new long[blockModel.getNumBlocks()][blockModel.getNumBlocks()];
        for(int i = 0; i < blockModel.getNumBlocks(); i+=1) {
            for(int j = i; j < blockModel.getNumBlocks(); j+=1) {
                if(i == j) {
                    long size = blockModel.getSizes()[i];
                    edgesOriginal[i][j] = (long)(blockModel.getProbabilities()[i][i]*(size*(size-1))/2);
                } else {
                    long sizei = blockModel.getSizes()[i];
                    long sizej = blockModel.getSizes()[j];
                    edgesOriginal[i][j] = (long)(blockModel.getProbabilities()[i][j] * (sizei * sizej));
                }
                currentScore+=Math.pow(edgesOriginal[i][j],2);
            }
            Arrays.fill(edgesCurrent[i],0L);
        }

        while(traversal.hasNext()) {
            long node = traversal.next();
            int partitionId = findBestPartition(node);
            partition.addToPartition(node,partitionId);
        }
    }

    private double score(int partitionId, long [] partitionNeighbors, long partitionCounts, long partitionCapacity) {
        double score = 0.0D;
        for(int i = 0; i < blockModel.getNumBlocks(); i+=1) {
            for(int j = i; j < blockModel.getNumBlocks(); j+=1) {
                long offset = 0L;
                if( i < j && j==partitionId) {
                    offset = partitionNeighbors[i];
                } else if (i == j && j == partitionId) {
                    offset = partitionNeighbors[j];
                } else if( i == partitionId && j != partitionId) {
                    offset = partitionNeighbors[j];
                }
                score+=Math.pow(Math.abs(edgesOriginal[i][j] - (edgesCurrent[i][j]+offset)),2);
            }
        }
        return Math.abs(currentScore - score)*(1.0D - partitionCounts / (double) partitionCapacity);
    }

    private int findBestPartition( long node) {

        long partitionNeighbors[] = new long[blockModel.getNumBlocks()];
        Arrays.fill(partitionNeighbors,0L);

        Set<Long> neighbors = graph.getNeighbors(node);
        for (Long neighbor : neighbors) {
            Integer i = partition.getNodePartition(neighbor);
            if (i != null) {
                partitionNeighbors[i]++;
            }
        }

        double bestScore = score(0,partitionNeighbors,partition.getPartitionSize(0),blockModel.getSizes()[0]);
        int  bestPartition = 0;
        for(int i = 1; i < blockModel.getNumBlocks(); i+=1) {
            double newScore = score(i,partitionNeighbors,partition.getPartitionSize(i),blockModel.getSizes()[i]);
            if(newScore > bestScore) {
               bestScore = newScore;
               bestPartition = i;
            }
        }

        if (bestScore == 0.0) {
            long maxRemaining = blockModel.getSizes()[0] - partition.getPartitionSize(0);
            for (int i = 1; i < blockModel.getNumBlocks(); i++) {
                long remaining = blockModel.getSizes()[i] - partition.getPartitionSize(i);
                if (remaining > maxRemaining) {
                    maxRemaining = remaining;
                    bestPartition = i;
                }
            }
        }

        currentScore = 0L;
        for(int i = 0; i < blockModel.getNumBlocks(); i+=1) {
                for(int j = i; j < blockModel.getNumBlocks(); j+=1) {
                    long offset = 0L;
                    if( i < j && j==bestPartition) {
                        offset = partitionNeighbors[i];
                    } else if (i == j && j == bestPartition) {
                        offset = partitionNeighbors[j];
                    } else if( i == bestPartition && j != bestPartition) {
                        offset = partitionNeighbors[j];
                    }
                    edgesCurrent[i][j] += offset;
                    currentScore+=Math.pow(Math.abs(edgesOriginal[i][j] - (edgesCurrent[i][j])),2);
                }

        }
        return bestPartition;
    }

    @Override
    public Partition getPartition() {
        return partition;
    }
}
