package org.dama.datasynth.graphreader.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by aprat on 18/06/15.
 */
public class Graph {
    private HashMap<Long,Set<Long>> adjacencies;
    private long numNodes = 0L;
    


    
    public Graph(  ) {
        this.adjacencies = new HashMap<>();
    }

    public void addEdge(long tail, long head) {
	    Set<Long> tailAdjacencies = this.adjacencies.get(tail);
	    if(tailAdjacencies==null)
	    {
		    numNodes++;
		    tailAdjacencies=new HashSet<>();
	    }
	    tailAdjacencies.add(head);
	    
    }
    
    public void adjacencyList(long tail, Set<Long> neighbors) throws Exception
    {
	   Set<Long> tailAdjacencies = this.adjacencies.get(tail);
	    if (tailAdjacencies!= null)
		    throw new Exception("Node "+tail+" already exists.");

	    numNodes++;
	    adjacencies.put(tail, neighbors);
    }

    public Set<Long> neighbors(Long nodeId) {
        return adjacencies.get(nodeId);
    }
}
