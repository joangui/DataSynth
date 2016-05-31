package org.dama.datasynth.program.schnappi.ast;

/**
 * Created by quim on 5/18/16.
 */
public class ExprNode extends Node {
    public ExprNode(String id){
        super(id);
        this.type = "EXPR";
    }
    public ExprNode(String id, Node n){
        this(id);
        this.addChild(n);
    }
    @Override
    public Node copy(){
        ExprNode en = new ExprNode(this.id);
        for(Node child : this.children){
            en.addChild(child.copy());
        }
        return en;
    }
}
