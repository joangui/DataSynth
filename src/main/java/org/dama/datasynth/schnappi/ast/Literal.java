package org.dama.datasynth.schnappi.ast;

/**
 * Created by aprat on 22/08/16.
 * Represents a Literal in the Schnappi Ast.
 */
public abstract class Literal extends Atomic {

    /**
     * Constructor
     * @param value The value of the literal
     */
    public Literal(String value) {
        super(value);
    }

    /**
     * Copy constructor
     * @param literal The literal to copy from
     */
    public Literal(Literal literal) {
        super(literal.getValue());
    }

    @Override
    public String toString() {
        return "<literal,"+value+">";
    }

    public abstract Object getObject();

}
