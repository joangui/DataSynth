package org.dama.datasynth.schnappi.ast;

/**
 * Created by quim on 5/17/16.
 */
public abstract class Node {

    public abstract void accept(Visitor visitor);

    public String getType() {
        return this.getClass().getSimpleName();
    }

    public abstract Node copy();

}
