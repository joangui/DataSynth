package org.dama.datasynth.test.graphreader.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by aprat on 18/06/15.
 */
public class Graph extends HashMap<Long, Set<Long>> {

	private long numEdges = 0L;

	public void addEdge(long tail, long head) {
		Set<Long> tailAdjacencies = get(tail);
		if (tailAdjacencies == null) {
			tailAdjacencies = new HashSet<>();
			put(tail, tailAdjacencies);
		}
		tailAdjacencies.add(head);
		numEdges++;

	}

	/*public Map<Long,Set<Long>> adjacencyList ()
	 {
	 return adjacencies;
	 }*/
	public void adjacencyList(long tail, Set<Long> neighbors) throws Exception {
		Set<Long> tailAdjacencies = get(tail);
		if (tailAdjacencies != null) {
			throw new Exception("Node " + tail + " already exists.");
		}

		put(tail, neighbors);
		numEdges += neighbors.size();
	}

	public Set<Long> neighbors(Long nodeId) {
		return get(nodeId);
	}

	public long numNodes() {
		return size();
	}

	public long numEdges() {
		return numEdges;
	}
}
