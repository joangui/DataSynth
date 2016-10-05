package org.dama.datasynth.schnappi.ast;

/**
 * Created by aprat on 24/08/16.
 * Represents a String literal in the Schnappi Ast.
 */
public class StringLiteral extends Literal {

    /**
     * Constructor
     * @param value The value to initialize the literal from
     */
    public StringLiteral(String value) {
        super(value);
    }

    /**
     * Copy constructor
     * @param literal The string literal to copy from
     */
    public StringLiteral(StringLiteral literal) {
        super(literal);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public StringLiteral clone() {
        return new StringLiteral(this);
    }

    @Override
    public java.lang.String toString() {
        return "<String,"+value+">";
    }

    @Override
    public Object getObject() {
        return getValue();
    }
}
