package org.dama.datasynth.schnappi.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quim on 5/18/16.
 * Represents a Binding in the Schnappi Ast
 */
public class Binding extends Atomic {

    private List<String> bindingChain = null;

    /**
     * Constructor
     * @param bindingChain The binding chain of the binding
     */
    public Binding(List<String> bindingChain) {
        super(bindingChain.toString());
        this.bindingChain = bindingChain;
    }

    /**
     * Copy constructor
     * @param binding The binding to copy from
     */
    public Binding(Binding binding) {
        super(binding.bindingChain.toString());
        this.bindingChain = new ArrayList<String>();
        this.bindingChain.addAll(binding.bindingChain);
    }

    /**
     * Gets the binding chain of the binding
     * @return The binding chain of the binding
     */
    public List<String> getBindingChain() {
        return bindingChain;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Binding copy() {
        return new Binding(this);
    }

    @Override
    public String toString() {
        return "<Binding,"+bindingChain.toString()+">";
    }
}
