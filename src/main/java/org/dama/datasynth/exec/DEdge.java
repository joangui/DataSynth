package org.dama.datasynth.exec;

/**
 * Created by quim on 5/10/16.
 */
public class DEdge {

    private Vertex      source;
    private Vertex      target;

    /**
     * Constructor
     * @param source The source vertex of the edge
     * @param target The target vertex of the edge
     */
    public DEdge(Vertex source, Vertex target){
        this.source = source;
        this.target = target;
    }

    /**
     * Gets the source vertex of the edge
     * @return The source of the edge
     */
    public Vertex getSource() {
        return source;
    }

    /**
     * Gets the target of the edge
     * @return The target of the edge
     */
    public Vertex getTarget() {
        return target;
    }


    @Override
    public String toString(){
        return source.toString() + "->" + target.toString();
    }

}
