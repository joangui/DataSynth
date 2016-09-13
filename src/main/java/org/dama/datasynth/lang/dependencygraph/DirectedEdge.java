package org.dama.datasynth.lang.dependencygraph;

/**
 * Created by quim on 5/10/16.
 * This class represents a directed edge in the dependency graph.
 */
public class DirectedEdge {
    private String name = "UNDEFINED";

    /**
     * Constructor
     * @param name The name of the edge
     */
    public DirectedEdge(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the edge
     * @return The name of the edge
     */
    public String getName() {
        return name;
    }

}
