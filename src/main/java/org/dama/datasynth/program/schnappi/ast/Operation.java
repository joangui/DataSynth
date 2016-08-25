package org.dama.datasynth.program.schnappi.ast;

import org.dama.datasynth.program.schnappi.ast.visitor.Visitor;

/**
 * Created by aprat on 22/08/16.
 */
public abstract class Operation extends Node {

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);

    }

    @Override
    public abstract Operation copy();
}
