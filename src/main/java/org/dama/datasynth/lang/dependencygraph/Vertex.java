package org.dama.datasynth.lang.dependencygraph;

import java.lang.annotation.Inherited;

/**
 * Created by quim on 5/10/16.
 */
public abstract class Vertex {

    @Inherited
    public @interface Schnappi {
       String name();
    }

    private String id;
    protected DependencyGraph graph = null;

    public Vertex(DependencyGraph graph, String str){
        this.graph = graph;
        this.id = str;
    }

    public Boolean compareTo(Vertex v){
        if(!(this.getClass() == v.getClass())) return false;
        return this.id.equalsIgnoreCase(v.getId());
    }


    @Schnappi(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return getClass().getSimpleName();
    }

    @Override
    public String toString(){
        return "[" + this.getId() + ","+getClass().getSimpleName()+"]";
    }

    public abstract void accept(DependencyGraphVisitor visitor);
}
