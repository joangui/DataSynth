package org.dama.datasynth.schnappi.ast;

import java.io.Serializable;

/**
 * Created by quim on 5/17/16.
 * Base abstract class for each node in the Schnappi Ast
 */
public abstract class Node implements Serializable {

    /**
     * Accept method for the Schnappi Ast visitors
     * @param visitor The visitor accepting the node.
     */
    public abstract <T> T accept(Visitor<T> visitor);

    /**
     * Gets the type of the node
     * @return The type of the node
     */
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
