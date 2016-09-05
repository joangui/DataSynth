package org.dama.datasynth.lang.dependencygraph;

/**
 * Created by aprat on 20/04/16.
 */
public class Entity extends Vertex  {

    /**
     * The entity name
     */
    private String name;
    private long numInstances;

    /**
     * The name of the entity
     * @graph The depenceny graph
     * @param name
     */
    public Entity(String name, long numInstances) {
        this.name = name;
        this.numInstances = numInstances;
    }

    /**
     * Gets the entity name
     * @return The entity name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the number of instances
     * @return The number of instances
     */
    public long getNumInstances() {
        return numInstances;
    }

    @Override
    public String toString(){
        return "[" + name + ","+getClass().getSimpleName()+"]";
    }

    @Override
    public void accept(DependencyGraphVisitor visitor) {
        visitor.visit(this);
    }
}
