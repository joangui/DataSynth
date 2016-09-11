package org.dama.datasynth.schnappi.ast;

/**
 * Created by aprat on 24/08/16.
 */
public class Atomic extends Expression {

    protected String value;

    public Atomic(String value) {
        this.value = value;
    }

    public Atomic(Atomic atomic) {
        this.value = atomic.value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public java.lang.String toString() {
        return "<any,"+value+">";
    }

    @Override
    public Atomic copy() {
        return new Atomic(this);
    }
}
