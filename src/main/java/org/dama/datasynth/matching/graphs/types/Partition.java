/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dama.datasynth.matching.graphs.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author joangui
 */
public class Partition extends HashMap<Long, Set<Long>> {

	Map<Long, Long> partitionDictionary = new HashMap<>();

	public void addToPartition(Long nodeId, Long partitionId) {
		Set<Long> partition = get(partitionId);
		if (partition == null) {
			partition = new HashSet<>();
			put(partitionId, partition);
		}
		partition.add(nodeId);
		partitionDictionary.put(nodeId, partitionId);
	}

	public long numPartitions() {
		return entrySet().size();
	}

	public long nodeToPartition(long nodeId) {
		return partitionDictionary.get(nodeId);
	}

	public int numNodes() {
		return partitionDictionary.entrySet().size();
	}

}
