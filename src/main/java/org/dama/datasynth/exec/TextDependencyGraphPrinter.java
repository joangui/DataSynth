package org.dama.datasynth.exec;

import org.dama.datasynth.DataSynth;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by aprat on 17/05/16.
 */
public class TextDependencyGraphPrinter extends DependencyGraphVisitor {

    private static final Logger logger= Logger.getLogger( DataSynth.class.getSimpleName() );

    private DependencyGraph graph;
    private Set<Vertex> visited = new HashSet<Vertex>();
    private int indents = -1;
    private StringBuilder strBuilder = new StringBuilder();

    private String indents(Boolean direct) {
        StringBuilder strBuilder = new StringBuilder();
        for(int i = 0; i < indents;++i) {
            strBuilder.append("\t");
        }
        if(indents != 0 ) {
            if(direct) {
                strBuilder.append("^--");
            } else {
                strBuilder.append("^**");
            }
        }
        return strBuilder.toString();
    }
    @Override
    public void visit(DependencyGraph graph) {
        this.graph = graph;
        super.visit(graph);
        logger.log(Level.FINE,"\n"+strBuilder.toString());
    }

    public void explode(Vertex vertex) {
        visited.add(vertex);
        for(DEdge edge : graph.incomingEdgesOf(vertex) ) {
            Vertex source = edge.getSource();
            source.accept(this);
        }
    }

    @Override
    public void visit(EntityTask entity) {
        indents++;
        strBuilder.append(indents(true)+entity.toString()+"\n");
        explode(entity);
        indents--;
    }

    @Override
    public void visit(AttributeTask attribute) {
        indents++;
        strBuilder.append(indents(true)+attribute.toString()+"\n");
        explode(attribute);
        indents--;
    }

    @Override
    public void visit(EdgeTask relation) {
        indents++;
        strBuilder.append(indents(true)+relation.toString()+"\n");
        explode(relation);
        indents--;
    }
}
