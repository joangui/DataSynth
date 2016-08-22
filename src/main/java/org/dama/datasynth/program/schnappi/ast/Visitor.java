package org.dama.datasynth.program.schnappi.ast;

import org.dama.datasynth.program.Ast;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by quim on 5/30/16.
 */
public interface Visitor {
    public abstract void visit(Node node);
    public abstract void visit(AssigNode n);
    public abstract void visit(AtomNode n);
    public abstract void visit(BindingNode n);
    public abstract void visit(ExprNode n);
    public abstract void visit(FuncNode n);
    public abstract void visit(ParamsNode n);
    public abstract void visit(SignatureNode n);
}
