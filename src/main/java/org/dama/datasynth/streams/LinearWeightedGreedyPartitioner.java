package org.dama.datasynth.streams;

import java.util.Set;

/**
 * Created by aprat on 27/02/17.
 */
public class LinearWeightedGreedyPartitioner implements EdgePartitioner {

    private long numNodes = 0L;
    private long numPartitions = 0L;
    private int vertexPartitions[] = null;
    private long partitionCapacity = 0L;
    private long partitionSize[] = null;
    private Graph graph = null;

    int partitionCounts[] = null;

    @Override
    public void initialize(long numNodes, long numPartitions) {
        this.numNodes = numNodes;
        this.numPartitions = numPartitions;
        this.vertexPartitions = new int[(int)numNodes];
        for(int i = 0; i < numNodes; ++i) {
            vertexPartitions[i] = -1;
        }
        this.graph = new Graph(numNodes);
        this.partitionCounts = new int[(int)numPartitions];
        this.partitionSize = new long[(int)numPartitions];
        for(int i =0; i < numPartitions; ++i) {
            partitionSize[i] = 0L;
        }
        this.partitionCapacity = numNodes/numPartitions;
    }

    private double score(int i)  {
        return partitionCounts[i]*((double)partitionSize[i]/(double)partitionCapacity);
    }

    private int findBestPartition( long node) {
        for(int i = 0; i < numPartitions; ++i) {
            partitionCounts[i] = 0;
        }
        Set<Long> neighbors = graph.neighbors(node);
        for( Long neighbor : neighbors) {
            partitionCounts[vertexPartitions[(int)((long)neighbor)]]++;
        }
        int max = 0;
        for( int i = 0; i < numPartitions; ++i) {
            if(score(i) > score(max)) max = i;
        }
        return max;
    }

    @Override
    public Edge partition(Edge edge) {
        Edge returnEdge = new Edge();
        if(vertexPartitions[(int)edge.tail] != -1) {
            returnEdge.tail = findBestPartition(edge.tail);
            vertexPartitions[(int)edge.tail] = (int)returnEdge.tail;
            partitionSize[(int)returnEdge.tail]++;
        }

        if(vertexPartitions[(int)edge.head] != -1) {
            returnEdge.head = findBestPartition(edge.head);
            vertexPartitions[(int)edge.head] = (int)returnEdge.head;
            partitionSize[(int)returnEdge.head]++;
        }
        graph.addEdge(edge.tail, edge.head);
        return returnEdge;
    }
}
