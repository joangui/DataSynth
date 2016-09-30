package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.common.Types;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by quim on 5/10/16.
 * Abstract class used to represent a vertex in the dependency graph.
 */
public abstract class Vertex {


    private static long nextId = 0;
    protected Map<String,Object> properties = null;

    /**
     * Constructor
     */
    public Vertex(){
        properties = new HashMap<String,Object>();
        properties.put("id",new Long(nextId));
        nextId++;
    }

    /**
     * Gets the unique id of the vertex
     * @return The unique id of the vertex
     */
     public long getId() {
        return (Long)(properties.get("id"));
    }

    /**
     * Gets the type of the vertex
     * @return A String with the type of the vertex.
     */
    public String getType() {
        return getClass().getSimpleName();
    }

    /**
     * Checks whether the vertex is of the given type
     * @param type The type to check for
     * @return True if the vertex is of the given type. False otherwise.
     */
    public boolean isType(String type) {
        return getType().compareTo(type) == 0;
    }

    public Map<String,Object> getProperties() {
        return properties;
    }

    @Override
    public String toString(){
        return "[" + this.getId() + ","+getClass().getSimpleName()+"]";
    }

    public abstract void accept(DependencyGraphVisitor visitor);
}
