package org.dama.datasynth.program.schnappi.ast;

import org.dama.datasynth.program.schnappi.ast.visitor.Visitor;

/**
 * Created by quim on 5/18/16.
 */
public class Binding extends Any {

    public Binding(String value) {
        super(value);
    }

    public Binding(Binding id) {
        super(id.getValue());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Binding copy() {
        return new Binding(this);
    }

    @Override
    public String toString() {
        return "<Binding,"+value+">";
    }
}
