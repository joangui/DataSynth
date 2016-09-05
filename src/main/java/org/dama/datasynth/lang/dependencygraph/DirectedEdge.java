package org.dama.datasynth.lang.dependencygraph;

/**
 * Created by quim on 5/10/16.
 */
public class DirectedEdge {
    private String name = "UNDEFINED";

    public DirectedEdge(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
