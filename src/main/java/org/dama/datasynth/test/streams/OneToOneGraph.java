package org.dama.datasynth.test.streams;

/**
 * Created by aprat on 27/02/17.
 */
public class OneToOneGraph implements GraphStream {

    private long numNodes = 0L;
    private long numEdges = 0L;
    private long nextEdge = 0L;

    public OneToOneGraph( long numNodes )  {
        this.numNodes = numNodes;
        this.numEdges = numNodes/2;
    }

    @Override
    public boolean hasNext() {
        if(nextEdge < numEdges) return true;
        return false;
    }

    @Override
    public Edge nextEdge() {
        Edge edge = new Edge();
        edge.tail = nextEdge;
        edge.head = nextEdge + numNodes/2;
        nextEdge++;
        return edge;
    }

}
