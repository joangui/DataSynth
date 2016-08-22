package org.dama.datasynth.program.solvers;

import org.dama.datasynth.exec.Vertex;
import org.dama.datasynth.program.Ast;
import org.dama.datasynth.program.schnappi.ast.*;
import org.dama.datasynth.program.schnappi.ast.Operation;
import org.dama.datasynth.program.schnappi.ast.visitor.SolverInstantiator;
import org.dama.datasynth.program.schnappi.ast.visitor.Visitor;

import java.util.List;

/**
 * Created by quim on 5/5/16.
 */
public class Solver extends Statement {

    public      Signature               signature;
    public      List<Binding>           bindings;
    public      Ast                     ast;

    public Solver(Signature signature, List<Binding> bindings, Ast ast) {
        this.signature = signature;
        this.bindings = bindings;
        this.ast = ast;
    }

    public List<Binding> getBindings() {
        return bindings;
    }

    public Signature getSignature() {
        return signature;
    }

    public Ast getOperations() {
        return ast;
    }

    public Ast instantiate(Vertex v){
        Ast bound = new Ast(this.ast);
        SolverInstantiator instantiator = new SolverInstantiator(this,v);
        for(Operation operation : bound.getStatements()) {
            operation.accept(instantiator);
        }
        return bound;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Statement copy() {
        return null;
    }
}
