package org.dama.datasynth.schnappi.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quim on 5/17/16.
 * Represents a Function call in the Schnappi ast
 */
public class Function extends Expression {

    private String name = null;
    private List<Expression> parameters = null;

    /**
     * Constructor
     * @param name The name of the function
     * @param parameters The parameters of the function
     */
    public Function(String name, List<Expression> parameters){
        this.name = name;
        this.parameters = parameters;
    }

    /**
     * Copy constructor
     * @param function The function to copy from
     */
    public Function(Function function) {
        this.name = function.name;
        this.parameters = new ArrayList<Expression>(function.parameters);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Function copy() {
        return new Function(this);
    }

    /**
     * Gets the parameters of the function
     * @return The parameters of the function
     */
    public List<Expression> getParameters() {
        return parameters;
    }

    /**
     * Adds the parameters to the parameters of the function
     * @param parameters The parameters to add
     */
    public void addParameters(List<Expression> parameters) {
        this.parameters.addAll(parameters);
    }

    /**
     * Sets a new parameter list for the function
     * @param parameters The new parameter list for the function
     */
    public void setParameters(List<Expression> parameters) {
        this.parameters = parameters;
    }

    /**
     * Gets the name of the function
     * @return The name of the function
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "<function,"+name+">";
    }


}