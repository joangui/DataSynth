package org.dama.datasynth.schnappi.ast;

/**
 * Created by aprat on 24/08/16.
 */
public class Any extends Expression {

    protected String value;

    public Any(String value) {
        this.value = value;
    }

    public Any(Any any) {
        this.value = any.value;
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
    public Any copy() {
        return new Any(this);
    }
}
