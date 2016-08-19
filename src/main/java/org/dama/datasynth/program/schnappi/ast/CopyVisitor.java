package org.dama.datasynth.program.schnappi.ast;

import org.dama.datasynth.program.Ast;

/**
 * Created by quim on 5/30/16.
 */
public class CopyVisitor extends Visitor{
    @Override
    public void visit(AssigNode n) {
    }

    @Override
    public void visit(AtomNode n) {
    }

    @Override
    public void visit(BindingNode n) {
    }

    @Override
    public void visit(ExprNode n) {
    }

    @Override
    public void visit(FuncNode n) {
    }

    @Override
    public void visit(Node n) {
    }

    @Override
    public void visit(ParamsNode n) {
    }

    @Override
    public void visit(SignatureNode n) {
    }

    @Override
    public boolean actBefore(Ast ast) {
        return false;
    }

    @Override
    public void actAfter(Ast ast) {

    }

    @Override
    public boolean actBefore(Node node) {
        return false;
    }

    @Override
    public void actAfter(Node node) {

    }
}
