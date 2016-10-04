package org.dama.datasynth.schnappi.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quim on 5/18/16.
 * Represents a Binding in the Schnappi Ast
 */
public abstract class BindingExpression extends Expression {

    @Override
    public abstract BindingExpression copy();
}
