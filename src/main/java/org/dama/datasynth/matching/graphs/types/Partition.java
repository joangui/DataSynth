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
public class Partition extends HashMap<Integer, Set<Long>> {

	Map<Long, Integer> partitionDictionary = new HashMap<>();

	public void addToPartition(Long nodeId, Integer partitionId) {
		Set<Long> partition = get(partitionId);
		if (partition == null) {
			partition = new HashSet<>();
			put(partitionId, partition);
		}
		partition.add(nodeId);
		partitionDictionary.put(nodeId, partitionId);
	}

	public long getNumPartitions() {
		return entrySet().size();
	}

	public Integer getNodePartition(long nodeId) {
		return partitionDictionary.get(nodeId);
	}


	public int getNumNodes() {
		return partitionDictionary.entrySet().size();
	}

	public int getPartitionSize( int id ) {
	    Set<Long> ids = get(id);
	    if(ids != null) return ids.size();
	    return 0;
	}
}
