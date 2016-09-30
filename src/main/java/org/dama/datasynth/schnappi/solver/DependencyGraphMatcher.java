package org.dama.datasynth.schnappi.solver;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.dependencygraph.DependencyGraph;
import org.dama.datasynth.lang.dependencygraph.Vertex;
import org.dama.datasynth.schnappi.CompilerException;
import org.dama.datasynth.schnappi.ast.Binding;
import org.dama.datasynth.schnappi.ast.Expression;
import org.dama.datasynth.schnappi.ast.Id;
import org.dama.datasynth.schnappi.ast.StringLiteral;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aprat on 11/09/16.
 * Static class with helper functions to match against dependency graphs
 */
public class DependencyGraphMatcher {

    /**
     * Matches a binding chain agains a dependency graph
     * @param graph The dependency graph
     * @param vertex The vertex used in the binding chain
     * @param matchChain The binding Chain
     * @return A list of property values resulting from matching the chain
     */
    public static List<Object> match(DependencyGraph graph, Vertex vertex, List<String> matchChain) {
        List<Vertex> frontier = new ArrayList<Vertex>();
        frontier.add(vertex);
        for(int i = 1; i < matchChain.size()-1; ++i) {
            String edgeName = matchChain.get(i);
            List<Vertex> nextFrontier = new ArrayList<Vertex>();
            for(Vertex next : frontier) {
                nextFrontier.addAll(graph.getNeighbors(next,edgeName));
            }
            frontier = nextFrontier;
        }
        String propertyName = matchChain.get(matchChain.size()-1);
        List<Object> retList = new ArrayList<Object>();
        for(Vertex next : frontier) {
            Object value = next.getProperties().get(propertyName);
            if (value == null)
                throw new CompilerException(CompilerException.CompilerExceptionType.UNEXISITING_VERTEX_PROPERTY, propertyName + " in vertex of type " + next.getType());
            retList.add(value);
        }
        return retList;
    }
}
