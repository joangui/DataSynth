package org.dama.datasynth.lang.semanticcheckpasses;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.AstVisitor;
import org.dama.datasynth.lang.SemanticException;

/**
 * Created by aprat on 6/09/16.
 */
public class GeneratorRunParametersValid extends AstVisitor<Ast.Node> {

    private Ast ast = null;
    private Ast.Node context = null;
    public void check(Ast ast) {
        this.ast = ast;
        for(Ast.Entity entity : ast.getEntities().values()) {
            context = entity;
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
    public Ast.Node visit(Ast.Attribute attribute) {
        attribute.getGenerator().accept(this);
        return attribute;
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
        edge.getCorrellation().accept(this);
        return edge;
    }

    @Override
    public Ast.Node visit(Ast.Generator generator) {
        for(Ast.Atomic parameter : generator.getRunParameters()) {
            if(context instanceof Ast.Entity) {
                Ast.Entity entity = (Ast.Entity) context;
                if(entity.getAttributes().get(parameter.getName()) == null) {
                    throw new SemanticException(SemanticException.SemanticExceptionType.ATTRIBUTE_NAME_UNEXISTING,parameter.getName()+" in "+entity.getName());
                }
            } else if( context instanceof Ast.Edge) {
                Ast.Edge edge = (Ast.Edge) context;
                Ast.Entity source = ast.getEntities().get(edge.getSource());
                if(source.getAttributes().get(parameter.getName()) == null) {
                    throw new SemanticException(SemanticException.SemanticExceptionType.ATTRIBUTE_NAME_UNEXISTING,parameter.getName()+" in "+source.getName());
                }
            }
        }
        return generator;
    }

}
