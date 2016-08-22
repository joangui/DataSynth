package org.dama.datasynth.program.schnappi.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quim on 5/17/16.
 */
public class Node {

    public String           id = null;
    public String           type = "node";
    public ArrayList<Node>  children;

    public Node(String opp, Node... args){
        this.id = opp;
        this.children = new ArrayList<>();
        for(Node arg : args) this.children.add(new Node(arg));
    }

    public Node(String iid, String ttype){
        this.id = iid;
        this.type = ttype;
        this.children = new ArrayList<>();
    }

    public Node(Node n){
        this.id = n.id;
        this.type = n.type;
        this.children = new ArrayList<Node>(n.children);
    }

    public Node copy(){
        Node nou = new Node(this.id, this.type);
        for(Node child : this.children){
            nou.addChild(child.copy());
        }
        return nou;
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

    public List<Node> neighbors() {
        return children;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
