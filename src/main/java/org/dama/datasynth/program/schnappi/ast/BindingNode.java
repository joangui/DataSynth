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
    @Override
    public Node copy(){
        BindingNode bn = new BindingNode(this.id, this.type);
        bn.lhs = this.lhs;
        bn.rhs = this.rhs;
        for(Node child : this.children){
            bn.addChild(child.copy());
        }
        return bn;
    }
    public String toString() {
        return "<" + lhs + " = " + rhs + "," + type + ">";
    }

}
