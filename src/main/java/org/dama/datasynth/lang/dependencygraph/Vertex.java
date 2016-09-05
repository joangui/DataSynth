package org.dama.datasynth.lang.dependencygraph;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by quim on 5/10/16.
 */
public abstract class Vertex {

    private static int nextId = 0;

    private int id;

    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface Schnappi {
       String name();
    }


    /**
     * Constructor
     */
    public Vertex(){
        this.id = nextId++;
    }

    /**
     * Gets the unique id of the vertex
     * @return The unique id of the vertex
     */
    @Schnappi(name = "id")
    public int getId() {
        return id;
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

    @Override
    public String toString(){
        return "[" + this.getId() + ","+getClass().getSimpleName()+"]";
    }

    public abstract void accept(DependencyGraphVisitor visitor);
}
