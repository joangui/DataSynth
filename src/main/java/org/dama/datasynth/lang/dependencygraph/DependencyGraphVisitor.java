package org.dama.datasynth.lang.dependencygraph;

/**
 * Created by aprat on 17/05/16.
 */
public abstract class DependencyGraphVisitor {

    protected DependencyGraph graph = null;

    public DependencyGraphVisitor( DependencyGraph graph) {
        this.graph = graph;
    }

    /**
     * Performs the visit of a vertex of type Entity
     * @param entity The entity vertex to visit
     */
    public abstract void visit(Entity entity);

    /**
     * Performs the visit of a vertex of type Attribute
     * @param attribute The attribute vertex to visit
     */
    public abstract void visit(Attribute attribute);

    /**
     * Performs the visit of a vertex of type relation
     * @param relation The relation vertex to visit
     */
    public abstract void visit(Edge relation);
}
