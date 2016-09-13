package org.dama.datasynth.schnappi.ast;

/**
 * Created by aprat on 24/08/16.
 * This class represents a Variable in the Schnappi Ast.
 */
public class Var extends Atomic {
    public Var(String value) {
        super(value);
    }

    public Var(Var id) {
        super(id.getValue());
    }

    @Override
    public Var copy() {
        return new Var(this);
    }

    @Override
    public String toString() {
        return "<var,"+value+">";
    }
}
