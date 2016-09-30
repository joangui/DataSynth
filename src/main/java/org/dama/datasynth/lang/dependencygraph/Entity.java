package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.common.Types;

/**
 * Created by aprat on 20/04/16.
 * Represents an entity element in the dependency graph
 */
public class Entity extends Vertex  {


    /**
     * Constructor
     * @param name The name of the entity
     * @param numInstances The number of instances to generate the entity
     */
    public Entity(String name, long numInstances) {
        properties.put("name", new Types.Id(name,false));
        properties.put("number", numInstances);
    }

    /**
     * Gets the entity name
     * @return The entity name
     */
    public String getName(){
        return (String)properties.get("name").toString();
    }

    /**
     * Gets the number of instances
     * @return The number of instances
     */
    public long getNumInstances() {
        return (Long)(properties.get("number"));
    }

    @Override
    public String toString(){
        return "[" + getName() + ","+getClass().getSimpleName()+"]";
    }

    @Override
    public void accept(DependencyGraphVisitor visitor) {
        visitor.visit(this);
    }
}
