package org.dama.datasynth.program.schnappi.ast;

/**
 * Created by quim on 5/18/16.
 */
public class AtomNode extends Node{
    public AtomNode(String id, String type){
        super(id);
        this.type = type;
    }
    @Override
    public Node copy(){
        AtomNode an = new AtomNode(this.id, this.type);
        for(Node child : this.children){
            an.addChild(child.copy());
        }
        return an;
    }
}
