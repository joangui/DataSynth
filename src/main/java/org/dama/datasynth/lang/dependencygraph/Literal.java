package org.dama.datasynth.lang.dependencygraph;

/**
 * Created by aprat on 1/09/16.
 */
public class Literal extends Vertex {


    public Literal(DependencyGraph graph, String str) {
        super(graph, str);
    }

    public static enum Type {
        STRING,
        NUMBER
    }

    @Override
    public void accept(DependencyGraphVisitor visitor) {

    }


}
