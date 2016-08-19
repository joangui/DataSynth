package org.dama.datasynth.program;

import org.dama.datasynth.program.schnappi.ast.Node;
import org.dama.datasynth.utils.traversals.Tree;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by quim on 5/5/16.
 */
public class Ast implements Tree<Node> {
    private Node root;
    public Ast(Node r){
        this.root = r;
    }

    @Override
    public List<Node> getRoots(){
        List<Node> roots= new LinkedList<Node>();
        roots.add(root);
        return roots;
    }
    public String print(){
        return this.root.print();
    }
}
