package org.dama.datasynth.schnappi.ast;

/**
 * Created by quim on 5/18/16.
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

    public Atomic getId() {
        return id;
    }

    public void setId(Atomic id) {
        this.id = id;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }


    @Override
    public java.lang.String toString() {
        return "<assign>";
    }
}
