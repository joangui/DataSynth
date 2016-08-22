package org.dama.datasynth.program.schnappi.ast;

import org.dama.datasynth.DataSynth;
import org.dama.datasynth.program.Ast;

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

    private void explode(Node node) {
        for(Node neighbor : node.neighbors()) {
            neighbor.accept(this);
        }
    }

    @Override
    public void visit(AssigNode n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        explode(n);
        indents--;
    }

    @Override
    public void visit(AtomNode n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        explode(n);
        indents--;
    }

    @Override
    public void visit(BindingNode n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        explode(n);
        indents--;
    }

    @Override
    public void visit(ExprNode n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        explode(n);
        indents--;
    }

    @Override
    public void visit(FuncNode n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        explode(n);
        indents--;
    }

    @Override
    public void visit(ParamsNode n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        explode(n);
        indents--;
    }

    @Override
    public void visit(SignatureNode n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        explode(n);
        indents--;
    }

    @Override
    public void visit(Node n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        explode(n);
        indents--;
    }
}
