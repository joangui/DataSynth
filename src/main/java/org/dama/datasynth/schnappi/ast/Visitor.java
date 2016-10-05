package org.dama.datasynth.schnappi.ast;

import org.apache.commons.lang.NotImplementedException;
import org.dama.datasynth.schnappi.solver.Solver;

/**
 * Created by quim on 5/30/16.
 * Schnappi Ast visitor interface
 */
public abstract class Visitor<T> {

    public void call(Ast ast) {
        for(Operation operation : ast.getOperations()) {
            operation.accept(this);
        }
    }

    public T visit(Assign n) {
        throw new NotImplementedException();
    }
    public T visit(Binding n) {
        throw new NotImplementedException();
    }

    public T visit(Expression n){ throw new NotImplementedException();}
    public T visit(Function n){ throw new NotImplementedException();}
    public T visit(Signature n){ throw new NotImplementedException();}
    public T visit(Solver n){ throw new NotImplementedException();}
    public T visit(Operation n){ throw new NotImplementedException();}
    public T visit(Atomic n){ throw new NotImplementedException();}
    public T visit(Var n){ throw new NotImplementedException();}
    public T visit(Id n){ throw new NotImplementedException();}
    public T visit(StringLiteral n){ throw new NotImplementedException();}
    public T visit(Number n){ throw new NotImplementedException();}
    public T visit(BindingFunction n){ throw new NotImplementedException();}
}
