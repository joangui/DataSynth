package org.dama.datasynth.exec;

/**
 * Created by quim on 5/10/16.
 */
public abstract class Vertex {
    private String id;
    private String type;
    public Vertex(String str, String tpe){
        this.id = str;
        this.type = tpe;
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
}
