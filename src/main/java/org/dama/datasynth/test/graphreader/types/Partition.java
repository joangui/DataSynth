/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dama.datasynth.test.graphreader.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author joangui
 */
public class Partition extends HashMap<Long, Set<Long>> {

	private long numPartitions = 0L;
	private long numNodes = 0L;
	Map<Long, Long> partitionDictionary = new HashMap<>();

	public void addToPartition(Long nodeId, Long partitionId) {
		numNodes++;
		Set<Long> partition = get(partitionId);
		if (partition == null) {
			numPartitions++;
			partition = new HashSet<>();
		}
		partition.add(nodeId);
		put(partitionId, partition);
		partitionDictionary.put(nodeId, partitionId);
	}

	public long numPartitions() {
		return numPartitions;
	}

	public long numNodes() {
		return numNodes;
	}

	public long nodeToPartition(long nodeId) {
		return partitionDictionary.get(nodeId);
	}

}
