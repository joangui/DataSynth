package org.dama.datasynth.program.schnappi.ast;

/**
 * Created by quim on 5/30/16.
 */
public abstract class Visitor {
    public abstract Node visit(AssigNode n);
    public abstract Node visit(AtomNode n);
    public abstract Node visit(BindingNode n);
    public abstract Node visit(ExprNode n);
    public abstract Node visit(FuncNode n);
    public abstract Node visit(Node n);
    public abstract Node visit(ParamsNode n);
    public abstract Node visit(SignatureNode n);
}
