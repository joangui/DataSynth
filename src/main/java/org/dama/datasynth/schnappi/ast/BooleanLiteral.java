package org.dama.datasynth.schnappi.ast;

/**
 * Created by aprat on 24/08/16.
 * Represents a String literal in the Schnappi Ast.
 */
public class BooleanLiteral extends Literal {

    /**
     * Constructor
     * @param value The value to initialize the literal from
     */
    public BooleanLiteral(String value) {
        super(value);
    }

    /**
     * Copy constructor
     * @param literal The string literal to copy from
     */
    public BooleanLiteral(BooleanLiteral literal) {
        super(literal);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public BooleanLiteral clone() {
        return new BooleanLiteral(this);
    }

    @Override
    public String toString() {
        return "<Boolean,"+value+">";
    }

    @Override
    public Object getObject() {
        return getValue();
    }
}
