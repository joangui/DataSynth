package org.dama.datasynth.runtime.spark.operators.matching.graphs.types;

import org.dama.datasynth.runtime.spark.operators.matching.Table;
import org.dama.datasynth.runtime.spark.operators.matching.Tuple;

import java.util.HashMap;
import java.util.HashSet;
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

	/*public Map<Long,Set<Long>> setNeighbors ()
	 {
	 return adjacencies;
	 }*/
	public void setNeighbors(long tail, Set<Long> neighbors) throws Exception {
		Set<Long> tailAdjacencies = get(tail);
		if (tailAdjacencies != null) {
			throw new Exception("Node " + tail + " already exists.");
		}

		put(tail, neighbors);
		numEdges += neighbors.size();
	}

	public Set<Long> getNeighbors(Long nodeId) {
		return get(nodeId);
	}

	public long getNumNodes() {
		return size();
	}


	public Set<Long> getNodes() {
		return keySet();
	}

	public long numEdges() {
		return numEdges;
	}

	static public Graph fromTable(Table<Long,Long> table) {
		Graph graph = new Graph();
		for(Tuple<Long,Long> edge : table) {
			graph.addEdge(edge.getX(),edge.getY());
		}
		return graph;
	}

}
