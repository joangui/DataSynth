package org.dama.datasynth.lang.semanticcheckpasses;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.AstVisitor;
import org.dama.datasynth.lang.SemanticException;

/**
 * Created by aprat on 6/09/16.
 */
public class GeneratorRunParametersValid implements AstVisitor {

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
            Ast.Generator generator = attribute.getGenerator();
            for(Ast.Atomic attr : generator.getRunParameters()) {
                if (ast.getAttributes().get(entity.getName()+"."+attr.getName()) == null)
                    throw new SemanticException("Attribute does not exist.");
            }
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
    }

    @Override
    public void visit(Ast.Attribute attribute) {
        attribute.getGenerator().accept(this);
    }

    @Override
    public void visit(Ast.Atomic atomic) {

    }
}
