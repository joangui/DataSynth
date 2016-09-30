package org.dama.datasynth.lang.dependencygraph;

/**
 * Created by aprat on 1/09/16.
 * Represents a generator element in the dependency graph
 */
public class Generator extends Vertex {

    /**
     * Constructor
     * @param name  The name of the generator
     */
    public Generator(String name) {
        properties.put("name",name);
    }

    /**
     * Gets the name of the generator
     * @return The name of the generator
     */
    public String getName() {
        return (String)properties.get("name");
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
