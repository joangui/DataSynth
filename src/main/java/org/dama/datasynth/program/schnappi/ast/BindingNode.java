package org.dama.datasynth.program.schnappi.ast;

/**
 * Created by quim on 5/26/16.
 */
public class BindingNode extends Node{
    public String lhs;
    public String rhs;
    public BindingNode(String id, String type){
        super(id);
        this.type = type;
    }
    public String toString() {
        return "<" + lhs + " = " + rhs + "," + type + ">";
    }
}
