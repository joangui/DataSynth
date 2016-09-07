package org.dama.datasynth.schnappi.ast;

/**
 * Created by quim on 5/17/16.
 */
public class Function extends Expression {

    private String name = null;
    private Parameters parameters = null;

    public Function(String name, Parameters parameters){
        this.name = name;
        this.parameters = parameters;
    }

    public Function(Function function) {
        this.name = function.name;
        this.parameters = new Parameters(function.parameters);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Function copy() {
        return new Function(this);
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void addParameters(Parameters parameters) {
        for(Expression exp : parameters.getParams()) {
            this.parameters.addParam(exp);
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "<function,"+name+">";
    }


}