package org.dama.datasynth.schnappi.ast;

/**
 * Created by aprat on 22/08/16.
 */
public class Literal extends Atomic {

    public Literal(String value) {
        super(value);
    }

    public Literal(Literal literal) {
        super(literal.getValue());
    }

    @Override
    public Literal copy() {
        return new Literal(this);
    }

    @Override
    public java.lang.String toString() {
        return "<literal,"+value+">";
    }
}
