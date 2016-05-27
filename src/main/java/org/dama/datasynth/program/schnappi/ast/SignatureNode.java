package org.dama.datasynth.program.schnappi.ast;

/**
 * Created by quim on 5/25/16.
 */
public class SignatureNode extends Node {
    public SignatureNode(String id, String type){
        super(id);
        this.type = type;
    }
}
