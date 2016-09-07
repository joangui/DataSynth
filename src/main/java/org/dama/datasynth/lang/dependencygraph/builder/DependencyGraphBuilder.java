package org.dama.datasynth.lang.dependencygraph.builder;

import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.dependencygraph.DependencyGraph;
import org.dama.datasynth.lang.dependencygraph.builder.VerticesInserter;

/**
 * Created by aprat on 7/09/16.
 */
public class DependencyGraphBuilder {


    public static DependencyGraph buildDependencyGraph(Ast ast) {
        DependencyGraph graph = new DependencyGraph();
        VerticesInserter verticesInserter = new VerticesInserter();
        verticesInserter.run(graph,ast);
        CrossDependenciesInserter crossDependenciesInserter = new CrossDependenciesInserter();
        crossDependenciesInserter.run(graph,ast);
        return graph;
    }
}
