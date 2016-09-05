package org.dama.datasynth.lang.dependencygraph;

/**
 * Created by aprat on 1/09/16.
 */
public class Generator extends Vertex {

    private String name;

    public Generator(String name) {
        this.name = name;
    }

    @Override
    public void accept(DependencyGraphVisitor visitor) {
        visitor.visit(this);
    }

    public String getName() {
        return name;
    }
}
