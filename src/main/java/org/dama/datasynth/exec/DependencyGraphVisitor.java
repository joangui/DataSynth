package org.dama.datasynth.exec;

import org.jgrapht.DirectedGraph;

import java.util.*;

/**
 * Created by aprat on 17/05/16.
 */
public abstract class DependencyGraphVisitor {

    Set<Vertex>         visited     = new HashSet<Vertex>();

    /**
     * Visits the dependency graph in a depth first search way way
     * @param dG
     */
    public void visit(DependencyGraph dG) {
        if(!actBefore(dG)) return;
        List<Vertex>  toVisit = dG.getEntryPoints();
        for(Vertex v : toVisit) {
            recurse(dG,v);
        }
        actAfter(dG);
    }

    /**
     * Performs the recursion
     * @param dG The dependency graph
     * @param v The vertex to continue the recursion from
     */
    private void recurse(DependencyGraph dG, Vertex v) {
        if(!visited.contains(v)) {
            visited.add(v);
            if (!actBefore(v)) return;
            v.accept(this);
            for(DEdge edge : dG.incomingEdgesOf(v) ) {
                Vertex neighbor = edge.getSource();
                recurse(dG, neighbor);
            }
            actAfter(v);
        } else {
            if (!actBefore(v)) return;
            v.accept(this);
            actAfter(v);
        }
    }


    /**
     * Executed before starting the visit of the dependency graph
     * @param dG The dependency graph to visit
     * @return True if the visit must continue. False to abort
     */
    public abstract boolean actBefore(DependencyGraph dG);

    /**
     * Executed after the completion of the visiting process
     * @param dG The dependency graph visited
     */
    public abstract void actAfter(DependencyGraph dG);

    /**
     * Executed before accepting the visited vertex
     * @param v The vertex to actbefore from
     * @return True if the visit must continue through this vertex. False to abort
     */
    public abstract boolean actBefore(Vertex v);

    /**
     * Executed after accepting the visited vertex
     * @param v The vertex to actAfter from
     */
    public abstract void actAfter(Vertex v);

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
