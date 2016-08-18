package org.dama.datasynth.exec;

import org.dama.datasynth.lang.Ast;
import org.jgrapht.DirectedGraph;

/**
 * Created by quim on 5/12/16.
 */
public class DependencyGraph {

    private DirectedGraph<Vertex, DEdge> g;

    /**
     * Constructor
     * @param ast The ast to build the dependency graph from
     * @throws BuildDependencyGraphException
     */
    public DependencyGraph(Ast ast) throws BuildDependencyGraphException {
        GraphBuilder gb = new GraphBuilder(ast);
        this.g = gb.getG();
        TextDependencyGraphPrinter printer = new TextDependencyGraphPrinter();
        printer.visit(this);
    }

    public DirectedGraph<Vertex, DEdge> getG() {
        return g;
    }

    public void print(){
        for(DEdge e: g.edgeSet()){
            System.out.println(e.getSource().getType() + " :: " + e.getTarget().getType());
            System.out.println(e.getSource().getId() + " -> " + e.getTarget().getId());
        }
    }
}
