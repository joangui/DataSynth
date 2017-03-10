package org.dama.datasynth.schnappi.solver;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.dependencygraph.DependencyGraph;
import org.dama.datasynth.lang.dependencygraph.Vertex;
import org.dama.datasynth.schnappi.CompilerException;
import org.dama.datasynth.schnappi.ast.*;
import org.dama.datasynth.schnappi.ast.Number;

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
     * @param binding The binding Chain
     * @return A list of property values resulting from matching the chain
     */
    public static List<Object> match(DependencyGraph graph, Vertex vertex, Binding binding) {
        List<Vertex> frontier = new ArrayList<Vertex>();
        frontier.add(vertex);
        for(EdgeExpansion expansion : binding.getExpansionChain()) {
            List<Vertex> nextFrontier = new ArrayList<Vertex>();
            for(Vertex next : frontier) {
                if(expansion.getDirection() == Types.Direction.OUTGOING) {
                    nextFrontier.addAll(graph.getNeighbors(next, expansion.getName()));
                } else {
                    nextFrontier.addAll(graph.getIncomingNeighbors(next, expansion.getName()));
                }
            }
            nextFrontier.removeIf( p -> {
                for(BinaryExpression expr : expansion.getFilters()) {
                    if(!eval(graph,p,expr)) {
                       return true;
                    }
                }
                return false;
            });
            frontier = nextFrontier;
        }
        List<Object> retList = new ArrayList<Object>();
        for(Vertex next : frontier) {
            Object value = next.getProperties().get(binding.getLeaf());
            if (value == null)
                throw new CompilerException(CompilerException.CompilerExceptionType.UNEXISITING_VERTEX_PROPERTY, binding.getLeaf() + " in vertex of type " + next.getType());
            retList.add(value);
        }
        return retList;
    }

    /**
     * Evaluates the result of the expression
     */
    public static boolean eval(DependencyGraph graph, Vertex v, BinaryExpression expr) {
        switch(expr.getOperator()) {
            case EQ:
                return Types.compare(decodeExpression(graph,v,expr.getLeft()),decodeExpression(graph,v, expr.getRight()));
            case NEQ:
                return !Types.compare(decodeExpression(graph,v,expr.getLeft()),decodeExpression(graph,v, expr.getRight()));
        }
        return false;
    }

    public static Object decodeExpression(DependencyGraph graph, Vertex v, Expression expression)  {
        switch(expression.getType()) {
            case "BindingFunction":
                return decodeBindingFunction(graph,v,(BindingFunction)expression);
            case "Binding":
                List<Object> values = DependencyGraphMatcher.match(graph,v,((Binding)expression));
                if(values.size() != 1) throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_EXPRESSION,". Only univalued expressions allowed in signature");
                return values.get(0);
            case "Number":
                return Long.parseLong(((Number)expression).getValue());
            case "StringLiteral":
                return ((StringLiteral)expression).getValue();
            case "BooleanLiteral":
                return Boolean.parseBoolean(((BooleanLiteral)expression).getValue());
        }
        throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_EXPRESSION, ". Unsupported type "+expression.getType());
    }

    /**
     * Decodes an atomic
     * @param graph The graph to use to decode the atomic
     * @param v The vertex the atomic should be matched against in case it is a binding
     * @return The property value after decoding the atomic
     */

    public static Object decodeBindingFunction(DependencyGraph graph, Vertex v, BindingFunction function) {
        switch(function.getName()) {
            case "length":
                List<Object> values = DependencyGraphMatcher.match(graph,v,((Binding)function.getExpression()));
                return new Long(values.size());
            default:
                throw new CompilerException(CompilerException.CompilerExceptionType.INVALID_BINDING_EXPRESSION, ". Unsupported binding function "+function.getName());
        }

    }
}
