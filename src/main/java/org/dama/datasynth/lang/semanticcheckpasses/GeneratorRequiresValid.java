package org.dama.datasynth.lang.semanticcheckpasses;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.AstVisitor;
import org.dama.datasynth.lang.SemanticException;

/**
 * Created by aprat on 6/09/16.
 * Visitor that performs a semantic check over the ast.
 * Checks that the "requires" attributes of the generator are valid
 */
public class GeneratorRequiresValid extends AstVisitor<Ast.Node> {

    private Ast ast = null;
    private Ast.Node context = null;

    /**
     * Performs the check over the ast.
     * @param ast The ast to check
     */
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
        return edge;
    }

    @Override
    public Ast.Node visit(Ast.Generator generator) {
        for(Ast.Atomic parameter : generator.getRunParameters()) {
            if(context instanceof Ast.Entity) {
                Ast.Entity entity = (Ast.Entity) context;
                if(entity.getAttributes().get(parameter.getName()) == null && parameter.getName().compareTo(entity.getName()+".oid")!=0) {
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
