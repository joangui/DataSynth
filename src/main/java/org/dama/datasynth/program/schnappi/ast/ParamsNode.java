package org.dama.datasynth.program.schnappi.ast;

import java.util.ArrayList;

/**
 * Created by quim on 5/19/16.
 */
public class ParamsNode extends Node {
    public ArrayList<String> params;
    public ParamsNode(String id){
        super(id, "params");
        this.params = new ArrayList<>();
    }
    public ParamsNode(String param1, String param2){
        this("params");
        this.addParam(param1);
        this.addParam(param2);
    }
    public void mergeParams(ParamsNode n){
        this.params.addAll(n.params);
    }
    public void addParam(String id){
        this.params.add(id);
    }
    public String getParam(int i){
        return this.params.get(i);
    }
    @Override
    public String toString(){
        String str = "P<";
        if(params.size() > 0){
            String aux = "";
            for(String s : params){
                aux += ", " + s;
            }
            str += aux.substring(1);
        }else {
            str += "wtf " + this.id;
        }
        str += " >";
        return str;
    }
    @Override
    public Node copy(){
        ParamsNode pn = new ParamsNode(this.id);
        pn.params = new ArrayList<String>(this.params);
        for(Node child : this.children){
            pn.addChild(child.copy());
        }
        return pn;
    }
}
