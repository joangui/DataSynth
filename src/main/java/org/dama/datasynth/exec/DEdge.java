package org.dama.datasynth.exec;

/**
 * Created by quim on 5/10/16.
 */
public class DEdge {
    private Vertex source;
    private Vertex target;

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

    public DEdge(Vertex v1, Vertex v2){
        this.source = v1;
        this.target = v2;
    }

    @Override
    public String toString(){
        return source.toString() + "->" + target.toString();
    }

}
