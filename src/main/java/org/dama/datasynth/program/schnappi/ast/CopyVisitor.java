package org.dama.datasynth.program.schnappi.ast;

/**
 * Created by quim on 5/30/16.
 */
public class CopyVisitor extends Visitor{
    @Override
    public Node visit(AssigNode n) {
        return null;
    }

    @Override
    public Node visit(AtomNode n) {
        return null;
    }

    @Override
    public Node visit(BindingNode n) {
        return null;
    }

    @Override
    public Node visit(ExprNode n) {
        return null;
    }

    @Override
    public Node visit(FuncNode n) {
        return null;
    }

    @Override
    public Node visit(Node n) {
        return null;
    }

    @Override
    public Node visit(ParamsNode n) {
        return null;
    }

    @Override
    public Node visit(SignatureNode n) {
        return null;
    }
}
