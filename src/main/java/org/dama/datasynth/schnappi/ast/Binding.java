package org.dama.datasynth.schnappi.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quim on 5/18/16.
 */
public class Binding extends Atomic {

    private List<String> bindingChain = null;

    public Binding(List<String> bindingChain) {
        super(bindingChain.toString());
        this.bindingChain = bindingChain;
    }

    public Binding(Binding id) {
        super(id.bindingChain.toString());
        this.bindingChain = new ArrayList<String>();
        this.bindingChain.addAll(id.bindingChain);
    }

    public List<String> getBindingChain() {
        return bindingChain;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
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
