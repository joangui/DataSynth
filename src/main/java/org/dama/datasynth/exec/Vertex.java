package org.dama.datasynth.exec;

import org.dama.datasynth.program.solvers.SignatureVertex;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by quim on 5/10/16.
 */
public abstract class Vertex {
    private String id;
    private String type;
    private SignatureVertex signature;

    public Vertex(String str, String tpe){
        this.id = str;
        this.type = tpe;
        this.signature = new SignatureVertex(type);
    }
    public Boolean compareTo(Vertex v){
        if(!this.type.equalsIgnoreCase(v.getType())) return false;
        return this.id.equalsIgnoreCase(v.getId());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SignatureVertex getSignature() {
        return signature;
    }

    @Override
    public String toString(){
        return "[" + this.getId() + ", " + this.getType() + "]";
    }

    /*public List<Vertex> neighbors() {
        List<Vertex> neighbors = new LinkedList<Vertex>();
        for(DEdge edge : graph.incomingEdgesOf(this)) {
            Vertex source = edge.getSource();
            neighbors.add(source);
        }
        return neighbors;
    }*/

    public abstract void accept(DependencyGraphVisitor visitor);
}
