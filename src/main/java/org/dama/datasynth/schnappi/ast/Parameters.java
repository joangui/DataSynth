package org.dama.datasynth.schnappi.ast;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by quim on 5/19/16.
 * Represents a list of parameters in the Schnappi Ast
 */
public class Parameters extends Node {

    public List<Expression> params = new LinkedList<Expression>();

    /**
     * Constructor
     * @param exprs The list of expressions used to initialize the parameters
     */
    public Parameters(Expression ... exprs){
        for(Expression expr : exprs) {
            params.add(expr);
        }
    }

    /**
     * Copy constructor
     * @param parameters The Parameters to copy from
     */
    public Parameters(Parameters parameters) {
        for(Expression param : parameters.params) {
            this.params.add((Expression)param.copy());
        }
    }

    /**
     * Merges this Parameters with another parameters
     * @param parameters The Parameters to merge from
     */
    public void mergeParams(Parameters parameters){
        this.params.addAll(parameters.params);
    }

    /**
     * Adds an expression to the parameters
     * @param expr
     */
    public void addParam(Expression expr){
        this.params.add(expr);
    }

    /**
     * Gets the parameter expression at a given position
     * @param i The position of the expression to get
     * @return The expression at position i
     */
    public Expression getParam(int i){
        return this.params.get(i);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public List<Expression> getParams() {
        return params;
    }

    @Override
    public Node copy() {
        return new Parameters(this);
    }

    @Override
    public String toString() {
        return "<Parameters>";
    }
}
