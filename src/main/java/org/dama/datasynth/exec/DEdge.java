package org.dama.datasynth.exec;

import org.dama.datasynth.program.solvers.Signature;

/**
 * Created by quim on 5/10/16.
 */
public class DEdge {

    private Vertex      source;
    private Vertex      target;
    private Signature   signature;

    /**
     * Constructor
     * @param source The source vertex of the edge
     * @param target The target vertex of the edge
     */
    public DEdge(Vertex source, Vertex target){
        this.source = source;
        this.target = target;
        this.signature = new Signature(source.getType(), target.getType());
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

    /**
     * Gets the signature of the edge
     * @return The signature of the edge
     */
    public Signature getSignature() {
        return signature;
    }


    @Override
    public String toString(){
        return source.toString() + "->" + target.toString();
    }

}
