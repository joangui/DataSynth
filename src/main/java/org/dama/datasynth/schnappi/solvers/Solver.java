package org.dama.datasynth.schnappi.solvers;

import org.dama.datasynth.lang.dependencygraph.Vertex;
import org.dama.datasynth.schnappi.ast.Ast;
import org.dama.datasynth.schnappi.ast.*;
import org.dama.datasynth.schnappi.ast.Operation;
import org.dama.datasynth.schnappi.compilerpass.SolverInstantiator;
import org.dama.datasynth.schnappi.ast.Visitor;

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
