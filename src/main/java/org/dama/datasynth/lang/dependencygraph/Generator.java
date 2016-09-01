package org.dama.datasynth.lang.dependencygraph;

/**
 * Created by aprat on 1/09/16.
 */
public class Generator extends Vertex {

    public Generator(DependencyGraph graph, String str) {
        super(graph, str);
    }

    @Override
    public void accept(DependencyGraphVisitor visitor) {
        visitor.visit(this);
    }
}
