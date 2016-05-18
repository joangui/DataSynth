package org.dama.datasynth.program.schnappi.ast;

import java.util.ArrayList;

/**
 * Created by quim on 5/17/16.
 */
public class Node {
    public String id;
    public String type = "node";
    public ArrayList<Node> children;
    public Node(String opp, Node... args){
        this.id = opp;
        this.children = new ArrayList<Node>();
        for(Node arg : args) this.children.add(new Node(arg));
    }
    public Node(Node n){
        this.id = n.id;
        this.children = new ArrayList<Node>(n.children);
    }
    public Node getChild(int i){
        return this.children.get(i);
    }
    public void addChild(Node n) {
        this.children.add(n);
    }
    public String toString(){
        return "<" + id + "," + type + ">";
    }
    public String toStringTabbed(String pad){
        String str = pad + this.toString();
        for(Node n : children){
            str = str + "\n" + ">" + pad + "\t";
            String aux = "";
            if(n != null) aux = n.toStringTabbed(pad+"\t");
            str = str + aux;
        }
        return str;
    }
}
