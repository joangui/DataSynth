package org.dama.datasynth.schnappi.ast;

/**
 * Created by aprat on 24/08/16.
 */
public class StringLiteral extends Literal {
    public StringLiteral(String value) {
        super(value);
    }

    public StringLiteral(StringLiteral literal) {
        super(literal);
    }

    @Override
    public StringLiteral copy() {
        return new StringLiteral(this);
    }

    @Override
    public java.lang.String toString() {
        return "<String,"+value+">";
    }
}
