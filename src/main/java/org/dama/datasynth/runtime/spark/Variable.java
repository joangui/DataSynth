package org.dama.datasynth.runtime.spark;

/**
 * Created by aprat on 13/09/16.
 */
public class Variable extends ExpressionValue implements Comparable<Variable> {

    private String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Variable o) {
       return name.compareTo(o.name);
    }
}
