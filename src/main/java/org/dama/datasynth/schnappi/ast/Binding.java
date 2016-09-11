package org.dama.datasynth.schnappi.ast;

/**
 * Created by quim on 5/18/16.
 */
public class Binding extends Atomic {

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
