package org.dama.datasynth.lang.semanticcheckpasses;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.AstVisitor;
import org.dama.datasynth.lang.SemanticException;

/**
 * Created by aprat on 6/09/16.
 */
public class EdgeEndpointsExist implements AstVisitor {

    private Ast ast = null;

    public void check(Ast ast) {
        this.ast = ast;
        for(Ast.Entity entity : ast.getEntities().values()) {
            entity.accept(this);
        }

        for(Ast.Edge edge : ast.getEdges().values()) {
            edge.accept(this);
        }
    }

    @Override
    public void visit(Ast.Entity entity) {
        for(Ast.Attribute attribute : entity.getAttributes()) {
            attribute.accept(this);
        }
    }

    @Override
    public void visit(Ast.Edge edge) {
        if(ast.getEntities().get(edge.getSource()) == null) {
            throw new SemanticException("Edge source "+edge.getSource()+" is not a valid entity name. It does not exist.");
        }

        if(ast.getEntities().get(edge.getTarget()) == null) {
            throw new SemanticException("Edge target "+edge.getTarget()+" is not a valid entity name. It does not exist.");
        }
    }

    @Override
    public void visit(Ast.Generator generator) {
    }

    @Override
    public void visit(Ast.Attribute attribute) {
        attribute.getGenerator().accept(this);
    }

    @Override
    public void visit(Ast.Atomic atomic) {

    }
}
