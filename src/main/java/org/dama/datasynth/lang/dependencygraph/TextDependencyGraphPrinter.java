package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.DataSynth;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by aprat on 17/05/16.
 */
public class TextDependencyGraphPrinter extends DependencyGraphVisitor {

    private static final Logger logger= Logger.getLogger( DataSynth.class.getSimpleName() );

    private int                 indents     = -1;


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
                strBuilder.append("|__");
            } else {
                strBuilder.append("|**");
            }
        }
        return strBuilder.toString();
    }

    private void explode(Vertex vertex) {
        List<Vertex> neighbors = new LinkedList<Vertex>();
        for(DEdge edge : graph.outgoingEdgesOf(vertex)) {
            Vertex source = edge.getTarget();
            source.accept(this);
        }
    }


    @Override
    public void visit(Entity entity) {
        indents++;
        logger.log(Level.FINE,new String(indents(true)+entity.toString()));
        explode(entity);
        indents--;
    }

    @Override
    public void visit(Attribute attribute) {
        indents++;
        logger.log(Level.FINE,new String(indents(true)+attribute.toString()));
        explode(attribute);
        indents--;
    }

    @Override
    public void visit(Edge relation) {
        indents++;
        logger.log(Level.FINE,new String(indents(true)+relation.toString()));
        explode(relation);
        indents--;
    }
}
