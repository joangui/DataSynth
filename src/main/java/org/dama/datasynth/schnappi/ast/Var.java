package org.dama.datasynth.schnappi.ast;

/**
 * Created by aprat on 24/08/16.
 * This class represents a Variable in the Schnappi Ast.
 */
public class Var extends Atomic implements Comparable<Var>{
    public Var(String value) {
        super(value);
    }

    public Var(Var id) {
        super(id.getValue());
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Var copy() {
        return new Var(this);
    }

    @Override
    public String toString() {
        return "<var,"+value+">";
    }

    @Override
    public int compareTo(Var o) {
        return getValue().compareTo(o.getValue());
    }

    @Override
    public boolean isAssignable() {
        return true;
    }
}
