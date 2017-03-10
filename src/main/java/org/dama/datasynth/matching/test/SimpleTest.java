/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dama.datasynth.matching.test;

import org.dama.datasynth.matching.Table;
import org.dama.datasynth.matching.Tuple;
import org.dama.datasynth.matching.graphs.GraphReaderFromNodePairFile;
import org.dama.datasynth.matching.graphs.types.Graph;
import org.dama.datasynth.matching.graphs.types.Partition;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author joangui
 */
public class SimpleTest {

	static Random r = new Random(1234567890L);
	static int NUM_ATTRIBUTES = 3;

	static public void main(String[] argv) throws Exception {
		System.out.println("Simple Test Run");
		Table<Long, Integer> attributes = new Table<>();
		Table<Long, Long> edges = new Table<>();

		GraphReaderFromNodePairFile graphReader = new GraphReaderFromNodePairFile(argv[0], argv[1]);

		Graph g = graphReader.getGraph();
		Partition p = graphReader.getPartitions(g);

		for (Map.Entry<Long, Set<Long>> entry : p.entrySet()) {
			Long partitionId = entry.getKey();
			Set<Long> nodes = entry.getValue();

			Integer attribute = getAttribute(NUM_ATTRIBUTES);
//			Integer attribute = partitionId%2==0?1:2;

			for (long nodeId : nodes) {
				attributes.add(new Tuple<>(nodeId, attribute));
			}
		}

		Map<Long, Table> edgesPartition = new HashMap<>();
		Table<Long, Long> edgesMixed = new Table<>();

		for (Map.Entry<Long, Set<Long>> entry : g.entrySet()) {
			long tailId = entry.getKey();
			long partitionTail = p.nodeToPartition(tailId);
			for (Long headId : entry.getValue()) {
				long partitionHead = p.nodeToPartition(headId);
				if (partitionTail == partitionHead) {
					Table<Long, Long> edgeTable = edgesPartition.get(partitionHead);
					if (edgeTable == null) {
						edgeTable = new Table<>();
						edgesPartition.put(partitionHead, edgeTable);
					}
					edgeTable.add(new Tuple<>(headId, tailId));
				} else {
					edgesMixed.add(new Tuple<>(headId, tailId));
				}

			}
		}

		for (Map.Entry<Long, Table> entry : edgesPartition.entrySet()) {
			Table<Long, Long> edgesTable = entry.getValue();
			for (Tuple<Long, Long> edge : edgesTable) {
				edges.add(edge);
			}
		}
		for (Tuple<Long, Long> edge : edgesMixed) {
			edges.add(edge);
		}


		
		MatchingCommunityTest.run(attributes, edges);
	}

	private static Integer getAttribute(int max) {
		return r.nextInt(max);

	}

}
