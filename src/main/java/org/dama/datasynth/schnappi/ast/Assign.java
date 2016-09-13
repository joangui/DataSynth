package org.dama.datasynth.schnappi.ast;

/**
 * Created by quim on 5/18/16.
 * Schnappi Ast node representing an assign operation
 */
public class Assign extends Operation {

    private Atomic id          = null;
    private Expression  expression  = null;

    public Assign(Atomic id, Expression expression){
        this.id = id;
        this.expression = expression;
    }

    public Assign(Assign assign) {
        this.id = assign.id.copy();
        this.expression = assign.expression.copy();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Operation copy() {
        return new Assign(this);
    }

    /**
     * Gets the Atomic of the left part of the assignment
     * @return The left part of the assignment.
     */
    public Atomic getId() {
        return id;
    }

    /**
     * Sets the Id of the left part of the assignment
     * @param id The id of the left part of the assignment
     */
    public void setId(Atomic id) {
        this.id = id;
    }

    /**
     * Gets the right expression of the assignment
     * @return The right expression of the assignment
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Sets the right expression of the assignment
     * @param expression The new right expression of the assignment
     */
    public void setExpression(Expression expression) {
        this.expression = expression;
    }


    @Override
    public java.lang.String toString() {
        return "<assign>";
    }
}