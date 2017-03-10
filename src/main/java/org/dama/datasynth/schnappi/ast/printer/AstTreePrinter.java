package org.dama.datasynth.schnappi.ast.printer;

import org.dama.datasynth.DataSynth;
import org.dama.datasynth.schnappi.ast.*;
import org.dama.datasynth.schnappi.ast.Number;
import org.dama.datasynth.schnappi.solver.Solver;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by aprat on 19/08/16.
 * Schnappi ast visitor that prints the ast in text format
 */
public class AstTreePrinter extends Visitor<Node> {

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
    public void call(Ast ast) {
        logger.log(Level.FINE, "Ast Tree Printer");
        logger.log(Level.FINE,"");
        super.call(ast);
        logger.log(Level.FINE,"");
    }

    @Override
    public Node visit(Assign n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        n.getId().accept(this);
        n.getExpression().accept(this);
        indents--;
        return null;
    }

    @Override
    public Node visit(Binding n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        indents--;
        return null;
    }

    @Override
    public Node visit(Function n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        for(Expression expr : n.getParameters()) {
            expr.accept(this);
        }
        indents--;
        return null;
    }

    @Override
    public Node visit(Signature n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        indents--;
        return null;
    }

    @Override
    public Node visit(Solver n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        indents--;
        return null;
    }

    @Override
    public Node visit(Operation n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        indents--;
        return null;
    }

    @Override
    public Node visit(Atomic n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        indents--;
        return null;
    }

    @Override
    public Node visit(Var n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        indents--;
        return null;
    }

    @Override
    public Node visit(Id n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        indents--;
        return null;
    }

    @Override
    public Node visit(StringLiteral n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        indents--;
        return null;
    }

    @Override
    public Node visit(Number n) {
        indents++;
        logger.log(Level.FINE,(indents(true)+n.toString()));
        indents--;
        return null;
    }
}
