package org.dama.datasynth.lang;

/**
 * Created by aprat on 2/09/16.
 */
public abstract class AstVisitor<T> {

    /**
     * Visits an entity
     * @param entity The entity to visit
     */
    public T visit(Ast.Entity entity) {
        throw new RuntimeException("Method not implemented");
    }

    /**
     * Visits an edge
     * @param edge the edge to visit
     */
    public T visit(Ast.Edge edge) {
        throw new RuntimeException("Method not implemented");
    }

    /**
     * Visits a generator
     * @param generator The generator to visit
     */
    public T visit(Ast.Generator generator) {
        throw new RuntimeException("Method not implemented");
    }

    /**
     * Visits an attribute
     * @param attribute The attribute to visit
     */
    public T visit(Ast.Attribute attribute) {
        throw new RuntimeException("Method not implemented");
    }

    /**
     * Visits an atomic
     * @param atomic The atomic to visit
     */
    public T visit(Ast.Atomic atomic) {
        throw new RuntimeException("Method not implemented");
    }

}
