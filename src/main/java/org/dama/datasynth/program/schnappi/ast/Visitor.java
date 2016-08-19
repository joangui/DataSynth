package org.dama.datasynth.program.schnappi.ast;

import org.dama.datasynth.program.Ast;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by quim on 5/30/16.
 */
public abstract class Visitor extends org.dama.datasynth.utils.traversals.Visitor<Ast, Node>{

    public abstract void visit(AssigNode n);
    public abstract void visit(AtomNode n);
    public abstract void visit(BindingNode n);
    public abstract void visit(ExprNode n);
    public abstract void visit(FuncNode n);
    public abstract void visit(Node n);
    public abstract void visit(ParamsNode n);
    public abstract void visit(SignatureNode n);
}
