package org.dama.datasynth.program;

import org.dama.datasynth.program.schnappi.ast.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by quim on 5/5/16.
 */
public class Ast {
    private Node root;
    public Ast(Node r){
        this.root = r;
    }

    public Node getRoot(){
        List<Node> roots= new LinkedList<Node>();
        roots.add(root);
        return root;
    }
}
