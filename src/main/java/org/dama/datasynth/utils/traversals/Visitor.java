package org.dama.datasynth.utils.traversals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by aprat on 19/08/16.
 */
public abstract class Visitor<T extends Tree<E>, E extends Acceptable<E> > {

    Set<E> visited     = new HashSet<E>();

    /**
     * Visits the dependency graph in a depth first search way way
     * @param dG
     */
    public void visit(T dG) {
        if(!actBefore(dG)) return;
        List<E> toVisit = dG.getRoots();
        for(E v : toVisit) {
            recurse(dG,v);
        }
        actAfter(dG);
    }

    /**
     * Performs the recursion
     * @param dG The dependency graph
     * @param v The vertex to continue the recursion from
     */
    private void recurse(T dG, E v) {
        if(!visited.contains(v)) {
            visited.add(v);
            if (!actBefore(v)) return;
            v.accept(this);
            for(E neighbor : v.neighbors() ) {
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
    public abstract boolean actBefore(T dG);

    /**
     * Executed after the completion of the visiting process
     * @param dG The dependency graph visited
     */
    public abstract void actAfter(T dG);

    /**
     * Executed before accepting the visited vertex
     * @param v The vertex to actbefore from
     * @return True if the visit must continue through this vertex. False to abort
     */
    public abstract boolean actBefore(E v);

    /**
     * Executed after accepting the visited vertex
     * @param v The vertex to actAfter from
     */
    public abstract void actAfter(E v);

}
