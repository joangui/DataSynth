package org.dama.datasynth.exec;

import org.dama.datasynth.program.solvers.SignatureVertex;
import org.dama.datasynth.utils.traversals.Acceptable;
import org.dama.datasynth.utils.traversals.Visitor;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by quim on 5/10/16.
 */
public abstract class Vertex implements Acceptable<Vertex> {
    private String id;
    private String type;
    private SignatureVertex signature;
    DependencyGraph graph = null;

    public Vertex(DependencyGraph graph, String str, String tpe){
        this.graph=graph;
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

    @Override
    public List<Vertex> neighbors() {
        List<Vertex> neighbors = new LinkedList<Vertex>();
        for(DEdge edge : graph.incomingEdgesOf(this)) {
            Vertex source = edge.getSource();
            neighbors.add(source);
        }
        return neighbors;
    }
}
