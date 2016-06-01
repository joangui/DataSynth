package org.dama.datasynth.program;

import org.dama.datasynth.program.schnappi.ast.Node;

/**
 * Created by quim on 5/5/16.
 */
public class Ast {
    private Node root;
    public Ast(Node r){
        this.root = r;
    }
    public Node getRoot(){
        return root;
    }
    public String print(){
        return this.root.print();
    }
}
