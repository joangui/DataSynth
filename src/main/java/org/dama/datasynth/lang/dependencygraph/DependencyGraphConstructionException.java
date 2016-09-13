package org.dama.datasynth.lang.dependencygraph;

/**
 * Created by aprat on 2/09/16.
 * Represents and exception during the constructor of a dependency graph.
 */
public class DependencyGraphConstructionException extends RuntimeException{
    public DependencyGraphConstructionException(String message) {
        super(message);
    }
}
