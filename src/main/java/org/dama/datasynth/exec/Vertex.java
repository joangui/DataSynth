package org.dama.datasynth.exec;

/**
 * Created by quim on 5/10/16.
 */
public abstract class Vertex {

    private String id;

    public Vertex(String str){
        this.id = str;
    }
    public Boolean compareTo(Vertex v){
        if(!(this.getClass() == v.getClass())) return false;
        return this.id.equalsIgnoreCase(v.getId());
    }

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
