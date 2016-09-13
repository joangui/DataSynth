package org.dama.datasynth.schnappi.ast;

/**
 * Created by aprat on 24/08/16.
 * Represents an Id in the Schnappi Ast
 */
public class Id extends Atomic {

    /**
     * Constructor
     * @param name The name of the Id
     */
    public Id(String name) {
        super(name);
    }

    /**
     * Copy constructor
     * @param id The id to copy from
     */
    public Id(Id id) {
        super(id.getValue());
    }

    @Override
    public Id copy() {
        return new Id(this);
    }

    @Override
    public String toString() {
        return "<id,"+value+">";
    }
}
