package org.dama.datasynth.lang.semanticcheckpasses;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.AstVisitor;
import org.dama.datasynth.lang.SemanticException;

/**
 * Created by aprat on 6/09/16.
 */
public class GeneratorsExist implements AstVisitor {

    public void check(Ast ast) {
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
        Ast.Generator sourceCardinalityGenerator = edge.getSourceCardinalityGenerator();
        if(sourceCardinalityGenerator != null) {
            sourceCardinalityGenerator.accept(this);
        }

        Ast.Generator targetCardinalityGenerator = edge.getTargetCardinalityGenerator();
        if(targetCardinalityGenerator != null) {
            targetCardinalityGenerator.accept(this);
        }
    }

    @Override
    public void visit(Ast.Generator generator) {
        try {
            Types.getGenerator(generator.getName());
        }
        catch ( Exception e) {
            throw new SemanticException("Generator \""+generator.getName()+"\" not found");
        }
    }

    @Override
    public void visit(Ast.Attribute attribute) {
        attribute.getGenerator().accept(this);
    }

    @Override
    public void visit(Ast.Atomic atomic) {

    }
}
