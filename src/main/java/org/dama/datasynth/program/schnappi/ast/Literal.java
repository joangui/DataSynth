package org.dama.datasynth.program.schnappi.ast;

import org.dama.datasynth.program.schnappi.ast.visitor.Visitor;

/**
 * Created by aprat on 22/08/16.
 */
public class Literal extends Expression {

    private String value;

    public Literal(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Expression copy() {
        return null;
    }
}
