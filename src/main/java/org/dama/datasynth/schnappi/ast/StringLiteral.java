package org.dama.datasynth.schnappi.ast;

/**
 * Created by aprat on 24/08/16.
 */
public class StringLiteral extends Literal {
    public StringLiteral(String value) {
        super(value);
    }

    public StringLiteral(StringLiteral literal) {
        super(literal.getValue());
    }

    @Override
    public java.lang.String toString() {
        return "<string,"+value+">";
    }
}
