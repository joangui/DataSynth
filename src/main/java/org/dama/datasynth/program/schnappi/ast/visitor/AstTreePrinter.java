package org.dama.datasynth.program.schnappi.ast.visitor;

import org.dama.datasynth.DataSynth;
import org.dama.datasynth.program.schnappi.ast.*;
import org.dama.datasynth.program.solvers.Solver;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by aprat on 19/08/16.
 */
public class AstTreePrinter implements Visitor {

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
    public void visit(Assign n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        n.getId().accept(this);
        n.getExpression().accept(this);
        indents--;
    }

    @Override
    public void visit(Id n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        indents--;
    }

    @Override
    public void visit(Binding n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        indents--;
    }

    @Override
    public void visit(Expression n) {
    }

    @Override
    public void visit(Function n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        n.getParameters().accept(this);
        indents--;
    }

    @Override
    public void visit(Parameters n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        for(Expression param : n.getParams()) {
            param.accept(this);
        }
        indents--;
    }

    @Override
    public void visit(Signature n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        indents--;
    }

    @Override
    public void visit(Solver n) {

    }

    @Override
    public void visit(Operation n) {

    }
}
