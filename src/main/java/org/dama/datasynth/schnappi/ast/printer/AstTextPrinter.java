package org.dama.datasynth.schnappi.ast.printer;

import org.dama.datasynth.DataSynth;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.schnappi.ast.*;
import org.dama.datasynth.schnappi.ast.Number;
import org.dama.datasynth.schnappi.ast.Visitor;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by aprat on 22/08/16.
 */
public class AstTextPrinter extends Visitor<String> {

    private static final Logger logger= Logger.getLogger( DataSynth.class.getSimpleName() );

    @Override
    public  String visit(Assign n) {
        StringBuffer buffer = new StringBuffer();
        if(n.getId().getType().compareTo("Var") == 0) {
            buffer.append("let ");
        }
        buffer.append(n.getId().accept(this) +" = "+n.getExpression().accept(this)+";");
        logger.log(Level.FINE,buffer.toString());
        return buffer.toString();
    }

    @Override
    public String visit(Binding n) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("@");
        buffer.append(n.getRoot());
        for(EdgeExpansion expansion : n.getExpansionChain()) {
            buffer.append(visit(expansion));
        }
        buffer.append("."+n.getLeaf());
        return buffer.toString();
    }

    @Override
    public String visit(EdgeExpansion n) {
        return (n.getDirection() == Types.Direction.OUTGOING ? "->" : "<-") + n.getName();
    }

    @Override
    public String visit(Function n) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(n.getName());
        buffer.append("(");
        if(n.getParameters().size()> 0) {
            Iterator<Expression> iter = n.getParameters().iterator();
            buffer.append(iter.next().accept(this));
            while(iter.hasNext()) {
                buffer.append(",");
                buffer.append(iter.next().accept(this));
            }
        }
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String visit(BinaryExpression n) {
        return n.getLeft().accept(this)+" "+n.getOperator().getText()+" "+n.getRight().accept(this);
    }

    @Override
    public String visit(Signature n) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("signature : {");
        for(Map.Entry<String,String> entry :  n.getBindings().entrySet()) {
            buffer.append("@"+entry.getKey()+" = "+entry.getValue()+";\n");
        }
        for(BinaryExpression operation : n.getOperations()) {
            buffer.append(operation.accept(this)+";\n");
        }
        buffer.append("}\n");
        return buffer.toString();
    }

    @Override
    public String visit(Var n) {
        return n.getValue();
    }

    @Override
    public String visit(Id n) {
        return "#"+n.getValue();
    }

    @Override
    public String visit(StringLiteral n) {
        return "\'"+n.getValue()+"\'";
    }

    @Override
    public String visit(Number n) {
        return n.getValue();
    }

    @Override
    public String visit(BindingFunction n) {
        StringBuilder builder = new StringBuilder();
        builder.append(n.getName()+"("+n.getExpression().accept(this)+")");
        return builder.toString();
    }

    @Override
    public void call(Ast ast) {
        logger.log(Level.FINE, "Ast Text Printer");
        logger.log(Level.FINE,"");
        super.call(ast);
        logger.log(Level.FINE,"");
    }
}
