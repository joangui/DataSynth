package org.dama.datasynth.schnappi.ast;

/**
 * Created by aprat on 24/08/16.
 * Represents an Atomic expression (single element) in the Schnappi Ast.
 */
public class Atomic extends Expression {

    protected String value;

    /**
     * Constructor
     * @param value The value of the atomic
     */
    public Atomic(String value) {
        this.value = value;
    }

    /**
     * Copy constructor
     * @param atomic The atomic to copy from
     */
    public Atomic(Atomic atomic) {
        this.value = atomic.value;
    }

    /**
     * Gets the value of teh atomic
     * @return The value of the atomic.
     */
    public String getValue() {
        return value;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public java.lang.String toString() {
        return "<any,"+value+">";
    }

    @Override
    public Atomic copy() {
        return new Atomic(this);
    }

    @Override
    public boolean equals(Object obj) {
        return hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }
}
