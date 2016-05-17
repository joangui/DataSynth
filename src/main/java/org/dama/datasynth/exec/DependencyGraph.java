package org.dama.datasynth.exec;

import org.dama.datasynth.lang.Ast;
import org.jgrapht.DirectedGraph;

/**
 * Created by quim on 5/12/16.
 */
public class DependencyGraph {
    private DirectedGraph<Vertex, DEdge> g;
    public DependencyGraph(Ast t) throws BuildDependencyGraphException {
        GraphBuilder gb = new GraphBuilder(t);
        this.g = gb.getG();
        TextDependencyGraphPrinter printer = new TextDependencyGraphPrinter();
        printer.visit(this);
    }

    public DirectedGraph<Vertex, DEdge> getG() {
        return g;
    }

    public void setG(DirectedGraph<Vertex, DEdge> g) {
        this.g = g;
    }
}
