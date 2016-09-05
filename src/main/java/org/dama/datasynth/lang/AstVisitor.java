package org.dama.datasynth.lang;

/**
 * Created by aprat on 2/09/16.
 */
public interface AstVisitor {

    /**
     * Visits an entity
     * @param entity The entity to visit
     */
    public void visit(Ast.Entity entity);

    /**
     * Visits an edge
     * @param edge the edge to visit
     */
    public void visit(Ast.Edge edge);

    /**
     * Visits a generator
     * @param generator The generator to visit
     */
    public void visit(Ast.Generator generator);

    /**
     * Visits an attribute
     * @param attribute The attribute to visit
     */
    public void visit(Ast.Attribute attribute);

    /**
     * Visits an atomic
     * @param atomic The atomic to visit
     */
    public void visit(Ast.Atomic atomic);

}
