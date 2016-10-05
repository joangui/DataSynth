package org.dama.datasynth.schnappi.ast;

/**
 * Created by quim on 5/18/16.
 * Represents a Binding in the Schnappi Ast
 */
public class BindingFunction extends BindingExpression {

    private String name;
    private BindingExpression expression;

    /**
     * Constructor
     * @param name The name of the function
     * @param expression The expression inside the function's parentheses
     */
    public BindingFunction(String name, BindingExpression expression) {
        this.name = name;
        this.expression = expression;
    }

    /**
     * Copy constructor
     * @param binding The binding to copy from
     */
    public BindingFunction(BindingFunction binding) {
        this.name = binding.name;
        this.expression = binding.clone();
    }

    /**
     * Gets the name of the function
     * @return The name of the function
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the parameter expression of the function
     * @return The parameter expression of the function
     */
    public BindingExpression getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "<BindingFunction,"+name+">";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public BindingExpression clone() {
        return new BindingFunction(this);
    }
}
