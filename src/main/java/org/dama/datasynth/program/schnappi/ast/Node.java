package org.dama.datasynth.program.schnappi.ast;

import org.dama.datasynth.program.schnappi.ast.visitor.Visitor;

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
