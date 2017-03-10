package org.dama.datasynth.matching.graphs;

import org.dama.datasynth.matching.graphs.types.Graph;

/**
 * Created by aprat on 27/02/17.
 */
public class OneToOneGraph extends Graph {

    public OneToOneGraph( long numNodes )  {
        for(int i = 0; i < numNodes/2; i+=1) {
            addEdge(i,numNodes/2 + i);
            addEdge(numNodes/2 + i, i);
        }
    }
}
