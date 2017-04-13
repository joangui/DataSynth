package org.dama.datasynth.matching.graphs.types;

/**
 * Created by aprat on 27/02/17.
 */
public abstract class GraphPartitioner {

    protected  Graph graph = null;
    protected Traversal traversal = null;
    public GraphPartitioner(Graph graph, Class<? extends Traversal> traversalType) {
        this.graph = graph;
        try {
            traversal = traversalType.newInstance();
            traversal.initialize(this.graph);
        } catch ( Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract Partition getPartition();
}
