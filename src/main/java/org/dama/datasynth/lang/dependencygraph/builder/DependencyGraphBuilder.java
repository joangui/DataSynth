package org.dama.datasynth.lang.dependencygraph.builder;

import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.dependencygraph.DependencyGraph;

/**
 * Created by aprat on 7/09/16.
 * Builder to construct dependency graphs out of an ast.
 */
public class DependencyGraphBuilder {


    /**
     * Builds a DependencyGraph out of an Ast
     * @param ast The Ast to build the dependency graph from
     * @return The dependency graph.
     */
    public static DependencyGraph buildDependencyGraph(Ast ast) {
        DependencyGraph graph = new DependencyGraph();
        VerticesInserter verticesInserter = new VerticesInserter();
        verticesInserter.run(graph,ast);
        CrossDependenciesInserter crossDependenciesInserter = new CrossDependenciesInserter();
        crossDependenciesInserter.run(graph,ast);
        return graph;
    }
}
