package org.dama.datasynth.schnappi.ast;

/**
 * Created by quim on 5/17/16.
 * Base abstract class for each node in the Schnappi Ast
 */
public abstract class Node {

    /**
     * Accept method for the Schnappi Ast visitors
     * @param visitor The visitor accepting the node.
     */
    public abstract void accept(Visitor visitor);

    /**
     * Gets the type of the node
     * @return The type of the node
     */
    public String getType() {
        return this.getClass().getSimpleName();
    }

    /**
     * Returns a copy of the node
     * @return A copy of the node.
     */
    public abstract Node copy();

}
