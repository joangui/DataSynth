package org.dama.datasynth.lang.semanticcheckpasses;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.AstVisitor;
import org.dama.datasynth.lang.SemanticException;

/**
 * Created by aprat on 6/09/16.
 * Visitor that performs a semantic check over the ast.
 * Checks that the used generators exist.
 */
public class GeneratorsExist extends AstVisitor<Ast.Node> {

    public void check(Ast ast) {
        for(Ast.Entity entity : ast.getEntities().values()) {
            entity.accept(this);
        }

        for(Ast.Edge edge : ast.getEdges().values()) {
            edge.accept(this);
        }
    }

    @Override
    public Ast.Node visit(Ast.Entity entity) {
        for(Ast.Attribute attribute : entity.getAttributes().values()) {
            attribute.accept(this);
        }
        return entity;
    }

    @Override
    public Ast.Node visit(Ast.Edge edge) {
        Ast.Generator sourceCardinalityGenerator = edge.getSourceCardinalityGenerator();
        if(sourceCardinalityGenerator != null) {
            sourceCardinalityGenerator.accept(this);
        }

        Ast.Generator targetCardinalityGenerator = edge.getTargetCardinalityGenerator();
        if(targetCardinalityGenerator != null) {
            targetCardinalityGenerator.accept(this);
        }
        return edge;
    }

    @Override
    public Ast.Node visit(Ast.Generator generator) {
        try {
            Types.getGenerator(generator.getName());
        }
        catch ( Exception e) {
            throw new SemanticException(SemanticException.SemanticExceptionType.GENERATOR_NOT_EXISTS,generator.getName());
        }
        return generator;
    }

    @Override
    public Ast.Node visit(Ast.Attribute attribute) {
        attribute.getGenerator().accept(this);
        return attribute;
    }

}
