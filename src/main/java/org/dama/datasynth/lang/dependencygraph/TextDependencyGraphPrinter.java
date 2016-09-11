package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.DataSynth;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by aprat on 17/05/16.
 */
public class TextDependencyGraphPrinter extends DependencyGraphVisitor {

    private static final Logger logger= Logger.getLogger( DataSynth.class.getSimpleName() );

    private int                 indents     = -1;

    private Map<Long,Long> visited = new HashMap<Long,Long>();
    private Stack<String>  edgeNames = new Stack<String>();


    public TextDependencyGraphPrinter(DependencyGraph graph) {
        super(graph);
    }

    /**
     * Retunrs an indented string with the number of current indents;
     * @param direct The type of indent
     * @return A string with the corresponding indent
     */
    private String indents(Boolean direct) {
        StringBuilder strBuilder = new StringBuilder();
        for(int i = 0; i < indents;++i) {
            strBuilder.append("\t");
        }
        if(indents != 0 ) {
            if(direct) {
                strBuilder.append("|_{"+edgeNames.peek()+"}>");
            } else {
                strBuilder.append("|**");
            }
        }
        return strBuilder.toString();
    }

    private void explode(Vertex vertex) {
        for(DirectedEdge edge : graph.getEdges(vertex)) {
            edgeNames.push(edge.getName());
            graph.getEdgeTarget(edge).accept(this);
            edgeNames.pop();
        }
    }

    private void printVertex(Vertex vertex) {
        logger.log(Level.FINE, new String(indents(true) + vertex.toString()));
    }

    public void print() {

        List<Entity> entities = graph.getEntities();
        for(Entity entity : entities) {
            entity.accept(this);
        }

        List<Edge> edges = graph.getEdges();
        for(Edge edge : edges) {
            edge.accept(this);
        }

    }

    @Override
    public void visit(Entity entity) {
        indents++;
        printVertex(entity);
        if(visited.get(entity.getId()) == null) {
            visited.put(entity.getId(),1L);
            explode(entity);
        }
        indents--;
    }

    @Override
    public void visit(Attribute attribute) {
        indents++;
        printVertex(attribute);
        if(visited.get(attribute.getId()) == null) {
            visited.put(attribute.getId(), 1L);
            explode(attribute);
        }
        indents--;
    }

    @Override
    public void visit(Edge edge) {
        indents++;
        printVertex(edge);
        if(visited.get(edge.getId()) == null) {
            visited.put(edge.getId(), 1L);
            explode(edge);
        }
        indents--;
    }

    @Override
    public void visit(Generator generator) {
        indents++;
        printVertex(generator);
        if(visited.get(generator.getId()) == null) {
            visited.put(generator.getId(), 1L);
            explode(generator);
        }
        indents--;
    }

    @Override
    public void visit(Literal literal) {
        indents++;
        printVertex(literal);
        if(visited.get(literal.getId()) == null) {
            visited.put(literal.getId(), 1L);
            explode(literal);
        }
        indents--;
    }
}
