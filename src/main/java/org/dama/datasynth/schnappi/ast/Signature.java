package org.dama.datasynth.schnappi.ast;

import org.dama.datasynth.lang.dependencygraph.DependencyGraph;
import org.dama.datasynth.lang.dependencygraph.Vertex;
import org.dama.datasynth.schnappi.solver.DependencyGraphMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quim on 5/25/16.
 * Represents a solver signature in the Schnappi Ast
 */
public class Signature extends Node {

    private Map<String,String>  bindings = new HashMap<String,String>();
    private List<BinaryExpression>   operations = new ArrayList<BinaryExpression>();

    /**
     * Constructor
     */
    public Signature(){
    }

    /**
     * Copy constructor
     * @param signature The signature to copy from
     */
    public Signature(Signature signature) {
        bindings = new HashMap<String,String>(signature.bindings);
        for(BinaryExpression expression : signature.operations) {
            operations.add(new BinaryExpression(expression));
        }
    }

    /**
     * Adds a signature operation to the signature
     * @param expression The binding binary expression to add.
     */
    public void addOperation(BinaryExpression expression) {
        operations.add(expression);
    }

    /**
     * Adds a binding to the signature
     * @param name The name of the binding
     * @param type The type of the binding
     */
    public void addBinding(String name, String type) {
        bindings.put(name,type);
    }

    /**
     * Gets the bindings of the signature
     * @return The bindings of the signature
     */
    public Map<String, String> getBindings() {
        return bindings;
    }

    public List<BinaryExpression> getOperations() {
        return operations;
    }



    /**
     * Evaluates a vertex against this signature
     * @param graph The dependency graph used for the evaluation
     * @param v The vertex to evaluate
     * @return True if the signature holds for the vertex
     */
    public boolean eval(DependencyGraph graph, Vertex v) {
        for(BinaryExpression operation : operations) {
            if(!DependencyGraphMatcher.eval(graph,v,operation)){
                return false;
            }
        }
        return true;
    }


    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Node clone() {
        return new Signature(this);
    }
}
