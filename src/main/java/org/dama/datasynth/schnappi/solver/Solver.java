package org.dama.datasynth.schnappi.solver;

import org.dama.datasynth.lang.dependencygraph.DependencyGraph;
import org.dama.datasynth.lang.dependencygraph.Vertex;
import org.dama.datasynth.schnappi.ast.Ast;
import org.dama.datasynth.schnappi.ast.*;
import org.dama.datasynth.schnappi.ast.Operation;
import org.dama.datasynth.schnappi.compilerpass.SolverInstantiator;
import org.dama.datasynth.schnappi.ast.Visitor;

/**
 * Created by quim on 5/5/16.
 * Represents a Schnappi solver
 */
public class Solver extends Node {

    public      Signature               signature;
    public      Ast                     ast;

    /**
     * Constructor
     * @param signature The signature of the solver
     * @param ast The Ast of the solver
     */
    public Solver(Signature signature, Ast ast) {
        this.signature = signature;
        this.ast = ast;
    }

    /**
     * Gets the signature of the solver
     * @return The signature of the solver
     */
    public Signature getSignature() {
        return signature;
    }

    /**
     * Gets an ast instance of this solver
     * @param graph The dependency graph used to instantiate the solver
     * @param v The vertex the solver is trying to solve
     * @return
     */
    public Ast instantiate(DependencyGraph graph, Vertex v){
        Ast bound = new Ast(this.ast);
        SolverInstantiator instantiator = new SolverInstantiator(graph, this,v);
        for(Operation operation : bound.getOperations()) {
            operation.accept(instantiator);
        }
        return bound;
    }

    /**
     * Evaluates whether this solver can solve the vertex in the given dependency graph
     * @param graph The dependency graph
     * @param v The vertex to solve
     * @return True if the solver can solve the vertex in the given dependency graph
     */
    public boolean eval(DependencyGraph graph, Vertex v) {
        return signature.eval(graph,v);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Node copy() {
        return null;
    }
}
