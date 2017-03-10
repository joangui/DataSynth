package org.dama.datasynth.schnappi.ast;

/**
 * Created by quim on 5/18/16.
 * Represents a Binding in the Schnappi Ast
 */
public abstract class BindingExpression extends Expression {

    @Override
    public abstract BindingExpression clone();

}
