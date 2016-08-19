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

    private int                 indents     = -1;
    private StringBuilder       strBuilder  = new StringBuilder();

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

    @Override
    public void visit(DependencyGraph graph) {
        strBuilder.append("Printing Dependency Graph\n");
        super.visit(graph);
        logger.log(Level.FINE,"\n"+strBuilder.toString());
    }

    @Override
    public void visit(EntityTask entity) {
        strBuilder.append(indents(true)+entity.toString()+"\n");
    }

    @Override
    public void visit(AttributeTask attribute) {
        strBuilder.append(indents(true)+attribute.toString()+"\n");
    }

    @Override
    public void visit(EdgeTask relation) {
        strBuilder.append(indents(true)+relation.toString()+"\n");
    }

    @Override
    public boolean actBefore(DependencyGraph dG) {
        return true;
    }

    @Override
    public void actAfter(DependencyGraph dG) {

    }

    @Override
    public boolean actBefore(Vertex v) {
        indents++;
        return true;
    }

    @Override
    public void actAfter(Vertex v) {
        indents--;
    }
}
