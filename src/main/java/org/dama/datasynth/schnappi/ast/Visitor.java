package org.dama.datasynth.schnappi.ast;

import org.dama.datasynth.schnappi.solver.Solver;

/**
 * Created by quim on 5/30/16.
 */
public interface Visitor {
    public abstract void visit(Assign n);
    public abstract void visit(Binding n);
    public abstract void visit(Expression n);
    public abstract void visit(Function n);
    public abstract void visit(Parameters n);
    public abstract void visit(Signature n);
    public abstract void visit(Solver n);
    public abstract void visit(Operation n);
    public abstract void visit(Atomic n);
    public abstract void visit(Id n);
    public abstract void visit(StringLiteral n);
    public abstract void visit(Number n);
}
