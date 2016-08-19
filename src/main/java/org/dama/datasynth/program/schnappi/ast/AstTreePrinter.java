package org.dama.datasynth.program.schnappi.ast;

import org.dama.datasynth.DataSynth;
import org.dama.datasynth.program.Ast;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by aprat on 19/08/16.
 */
public class AstTreePrinter extends Visitor {

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
    public void visit(Ast ast) {
        strBuilder.append("Printing Dependency Graph\n");
        super.visit(ast);
        logger.log(Level.FINE,"\n"+strBuilder.toString());
    }

    @Override
    public boolean actBefore(Ast ast) {
        return true;
    }

    @Override
    public void actAfter(Ast ast) {

    }

    @Override
    public boolean actBefore(Node node) {
        indents++;
        return true;
    }

    @Override
    public void actAfter(Node node) {
        indents--;
    }

    public void visit(AssigNode n) {
        strBuilder.append(indents(true)+n.toString()+"\n");
    }

    public void visit(AtomNode n) {
        strBuilder.append(indents(true)+n.toString()+"\n");
    }

    public void visit(BindingNode n) {
        strBuilder.append(indents(true)+n.toString()+"\n");
    }

    public void visit(ExprNode n) {
        strBuilder.append(indents(true)+n.toString()+"\n");
    }

    public void visit(FuncNode n) {
        strBuilder.append(indents(true)+n.toString()+"\n");
    }

    public void visit(Node n) {
        strBuilder.append(indents(true)+n.toString()+"\n");
    }

    public void visit(ParamsNode n) {
        strBuilder.append(indents(true)+n.toString()+"\n");
    }

    public void visit(SignatureNode n) {
        strBuilder.append(indents(true)+n.toString()+"\n");
    }
}
