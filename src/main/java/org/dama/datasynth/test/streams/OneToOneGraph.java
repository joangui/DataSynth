package org.dama.datasynth.test.streams;

import org.dama.datasynth.test.graphreader.types.Edge;
import org.dama.datasynth.test.graphreader.types.Graph;

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
