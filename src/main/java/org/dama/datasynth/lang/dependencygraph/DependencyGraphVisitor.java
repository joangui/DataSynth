package org.dama.datasynth.lang.dependencygraph;

import org.apache.commons.lang.NotImplementedException;

/**
 * Created by aprat on 17/05/16.
 * A visitor of a dependency graph.
 */
public abstract class DependencyGraphVisitor {

    protected DependencyGraph graph = null;

    public DependencyGraphVisitor(DependencyGraph graph) {
        this.graph = graph;
    }

    /**
     * Performs the visit of a vertex of type Entity
     *
     * @param entity The entity vertex to visit
     */
    public void visit(Entity entity) {
        throw new NotImplementedException();
    }

    /**
     * Performs the visit of a vertex of type Attribute
     * @param attribute The attribute vertex to visit
     */
    public void visit(Attribute attribute)  {
        throw new NotImplementedException();
    }

    /**
     * Performs the visit of a vertex of type relation
     * @param relation The relation vertex to visit
     */
    public void visit(Edge relation) {
        throw new NotImplementedException();
    }

    /*
     * Performs the visit of a vertex of type generator
     * @param generator The generator vertex to visit
     */
    public void visit(Generator generator) {
        throw new NotImplementedException();
    }

    /*
     * Performs the visit of a vertex of type literal
     * @param generator The literal vertex to visit
     */
    public void visit(Literal literal) {
        throw new NotImplementedException();
    }
}
