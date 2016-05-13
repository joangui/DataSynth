package org.dama.datasynth.exec;

import org.dama.datasynth.program.solvers.Signature;

/**
 * Created by quim on 5/10/16.
 */
public class DEdge {
    private Vertex source;
    private Vertex target;
    private Signature signature;
    public Vertex getSource() {
        return source;
    }

    public void setSource(Vertex source) {
        this.source = source;
    }

    public Vertex getTarget() {
        return target;
    }

    public void setTarget(Vertex target) {
        this.target = target;
    }

    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    public DEdge(Vertex v1, Vertex v2){
        this.source = v1;
        this.target = v2;
        this.signature = new Signature(v1.getType(), v2.getType());
    }

    @Override
    public String toString(){
        return source.toString() + "->" + target.toString();
    }

}
