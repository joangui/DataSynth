package org.dama.datasynth.lang.semanticcheckpasses;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.AstVisitor;
import org.dama.datasynth.lang.SemanticException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by aprat on 6/09/16.
 */
public class AttributeValidName implements AstVisitor {

    private Set<String> observedAttributes = new HashSet<String>();

    public void check(Ast ast) {
        for(Ast.Entity entity : ast.getEntities().values()) {
            entity.accept(this);
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
    }

    @Override
    public void visit(Ast.Generator generator) {
    }

    @Override
    public void visit(Ast.Attribute attribute) {
        if(observedAttributes.add(attribute.getName()) == false) throw new SemanticException("Repeated attribute "+attribute.getName());
        if(attribute.getName().contains("oid")) throw new SemanticException("Invalid attribute name \"oid\". Reserved name.");
    }

    @Override
    public void visit(Ast.Atomic atomic) {

    }
}
