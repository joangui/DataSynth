package org.dama.datasynth.matching.graphs.types;

/**
 * Created by aprat on 9/03/17.
 */
public interface Traversal {
    public void initialize(Graph graph);
    public boolean hasNext();
    public Long next();
}
