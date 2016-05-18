package org.dama.datasynth.program.schnappi.ast;

/**
 * Created by quim on 5/18/16.
 */
public class ExprNode extends Node {
    public ExprNode(String id){
        super(id);
    }
    public ExprNode(String id, Node n){
        this(id);
        this.addChild(n);
    }
}
