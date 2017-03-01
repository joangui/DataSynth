package org.dama.datasynth.streams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by aprat on 18/06/15.
 */
public class Graph {
    private HashMap<Long,HashSet<Long>> adjacencies;
    private long numNodes = 0L;
    public Graph( long numNodes ) {
        this.numNodes = numNodes;
        this.adjacencies = new HashMap<Long,HashSet<Long>>();
        for(long i = 0; i < numNodes; ++i) {
            adjacencies.put(i, new HashSet<Long>());
        }
    }

    public void addEdge(long tail, long head) {
        adjacencies.get(tail).add(head);
        adjacencies.get(head).add(tail);
    }

    public Set<Long> neighbors(Long person) {
        return adjacencies.get(person);
    }
}
