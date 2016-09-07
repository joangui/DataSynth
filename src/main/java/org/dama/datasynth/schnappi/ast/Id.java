package org.dama.datasynth.schnappi.ast;

/**
 * Created by aprat on 24/08/16.
 */
public class Id extends Any {
    public Id(String value) {
        super(value);
    }

    public Id(Id id) {
        super(id.getValue());
    }

    @Override
    public Id copy() {
        return new Id(this);
    }

    @Override
    public java.lang.String toString() {
        return "<id,"+value+">";
    }
}
