package org.dama.datasynth.program.schnappi.ast;

/**
 * Created by quim on 5/18/16.
 */
public class AssigNode extends Node {
    public AssigNode(String id){
        super(id);
        this.type = "ASSIG";
    }
    public AssigNode(String id, ExprNode n){
        this(id);
        this.addChild(n);
    }
}
