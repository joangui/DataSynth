package org.dama.datasynth.lang.dependencygraph;

/**
 * Created by aprat on 20/04/16.
 */
public class Entity extends Vertex  {

    /**
     * The entity name
     */

    /**
     * The name of the entity
     * @graph The depenceny graph
     * @param name
     */
    public Entity(String name, long numInstances) {
        properties.put("name",new PropertyValue(name));
        properties.put("number", new PropertyValue(numInstances));
    }

    /**
     * Gets the entity name
     * @return The entity name
     */
    public String getName(){
        return properties.get("name").getValue();
    }

    /**
     * Gets the number of instances
     * @return The number of instances
     */
    public long getNumInstances() {
        return Long.parseLong(properties.get("number").getValue());
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
