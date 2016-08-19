package org.dama.datasynth.exec;

import org.dama.datasynth.utils.traversals.Visitor;
import org.jgrapht.DirectedGraph;

import java.util.*;

/**
 * Created by aprat on 17/05/16.
 */
public abstract class DependencyGraphVisitor extends Visitor<DependencyGraph, Vertex> {


    /**
     * Performs the visit of a vertex of type Entity
     * @param entity The entity vertex to visit
     */
    public abstract void visit(EntityTask entity);

    /**
     * Performs the visit of a vertex of type Attribute
     * @param attribute The attribute vertex to visit
     */
    public abstract void visit(AttributeTask attribute);

    /**
     * Performs the visit of a vertex of type relation
     * @param relation The relation vertex to visit
     */
    public abstract void visit(EdgeTask relation);
}
