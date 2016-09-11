package org.dama.datasynth.lang.dependencygraph;

/**
 * Created by aprat on 1/09/16.
 */
public class Generator extends Vertex {

    public Generator(String name) {
        properties.put("name",new PropertyValue(name));
    }

    public String getName() {
        return properties.get("name").getValue();
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
