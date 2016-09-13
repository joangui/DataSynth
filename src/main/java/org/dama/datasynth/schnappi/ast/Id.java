package org.dama.datasynth.schnappi.ast;

/**
 * Created by aprat on 24/08/16.
 * Represents an Id in the Schnappi Ast
 */
public class Id extends Atomic implements Comparable<Id> {

    private boolean isTemporal;

    /**
     * Constructor
     * @param name The name of the Id
     */
    public Id(String name, boolean isTemporal) {
        super(name);
        this.isTemporal = isTemporal;
    }

    /**
     * Copy constructor
     * @param id The id to copy from
     */
    public Id(Id id) {
        super(id.getValue());
    }

    public boolean isTemporal() {
        return isTemporal;
    }

    @Override
    public Id copy() {
        return new Id(this);
    }

    @Override
    public String toString() {
        return "<id,"+value+",isTemporal="+isTemporal+">";
    }

    @Override
    public int compareTo(Id o) {
        return getValue().compareTo(o.getValue());
    }
}
