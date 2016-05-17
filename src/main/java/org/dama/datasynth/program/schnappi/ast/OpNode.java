package org.dama.datasynth.program.schnappi.ast;

import java.util.ArrayList;

/**
 * Created by quim on 5/17/16.
 */
public class OpNode extends Node {
    public ArrayList<String> ids;
    public OpNode(String id, String id1, String id2){
        this(id);
        this.ids.add(id1);
        this.ids.add(id2);
    }
    public OpNode(String id){
        super(id);
        this.ids = new ArrayList<>();
    }

}
