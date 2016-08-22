package org.dama.datasynth.program.schnappi.ast;

import org.dama.datasynth.program.schnappi.ast.visitor.Visitor;

/**
 * Created by quim on 5/18/16.
 */
public class Assign extends Operation {

    private Id          id          = null;
    private Expression  expression  = null;

    public Assign(Id id, Expression expression){
        this.id = id;
        this.expression = expression;
    }

    public Assign(Assign assign) {
        this.id = new Id(assign.id);
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

    public Id getId() {
        return id;
    }

    public Expression getExpression() {
        return expression;
    }
}
