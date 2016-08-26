package org.dama.datasynth.program.solvers;

import org.dama.datasynth.lang.dependencygraph.Vertex;
import org.dama.datasynth.program.Ast;
import org.dama.datasynth.program.schnappi.ast.*;
import org.dama.datasynth.program.schnappi.ast.Operation;
import org.dama.datasynth.program.schnappi.compilerpass.SolverInstantiator;
import org.dama.datasynth.program.schnappi.ast.visitor.Visitor;

/**
 * Created by quim on 5/5/16.
 */
public class Solver extends Node {

    public      Signature               signature;
    public      Ast                     ast;

    public Solver(Signature signature, Ast ast) {
        this.signature = signature;
        this.ast = ast;
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
    public Node copy() {
        return null;
    }
}
