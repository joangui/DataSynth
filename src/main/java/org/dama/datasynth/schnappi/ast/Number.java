package org.dama.datasynth.schnappi.ast;

/**
 * Created by aprat on 24/08/16.
 */
public class Number extends Literal {
    public Number(String value) {
        super(value);
    }

    @Override
    public java.lang.String toString() {
        return "<number,"+value+">";
    }
}
