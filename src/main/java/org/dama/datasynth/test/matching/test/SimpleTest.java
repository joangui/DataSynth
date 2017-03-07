/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dama.datasynth.test.matching.test;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.dama.datasynth.test.graphreader.GraphReaderFromFile;
import org.dama.datasynth.test.graphreader.GraphReaderFromNodePairFile;
import org.dama.datasynth.test.graphreader.types.Graph;
import org.dama.datasynth.test.graphreader.types.Partition;
import org.dama.datasynth.test.matching.Dictionary;
import org.dama.datasynth.test.matching.Table;
import org.dama.datasynth.test.matching.Tuple;

/**
 *
 * @author joangui
 */
public class SimpleTest {
	static Random r = new Random(1234567890L);
	static int NUM_ATTRIBUTES=2;
	static public void main(String[] argv) throws Exception {
		System.out.println("Simple Test Run");
		Table<Long, Integer> attributes = null;
		Table<Long, Long> edges = null;

		GraphReaderFromNodePairFile graphReader = new GraphReaderFromNodePairFile(argv[0], argv[1]);

		Graph g = graphReader.getGraph();
		Partition p = graphReader.getPartitions(g);

		attributes = new Table<>();
		for (Map.Entry<Long, Set<Long>> entry : p.entrySet()) {
			Long partitionId = entry.getKey();
			Set<Long> nodes = entry.getValue();

			Integer attribute = getAttribute(NUM_ATTRIBUTES);
//			Integer attribute = partitionId%2==0?1:2;

			for (long nodeId : nodes) {
				attributes.add(new Tuple<>(nodeId, attribute));
			}
		}

		Dictionary<Long, Integer> dictonariAttributes = new Dictionary<>(attributes);

		Table<Long, Long> edgesA = new Table<>();
		Table<Long, Long> edgesB = new Table<>();
		Table<Long, Long> edgesMixed = new Table<>();
		//Map<Long, Set<Long>> adjacencyList = g.adjacencyList();
		for (Map.Entry<Long, Set<Long>> entry : g.entrySet()) {
			Long tailId = entry.getKey();
			for (Long headId : entry.getValue()) {
				if (dictonariAttributes.get(headId).equals("A") && dictonariAttributes.get(tailId).equals("A")) {
					edgesA.add(new Tuple<>(tailId, headId));
				} else if (dictonariAttributes.get(headId).equals("B") && dictonariAttributes.get(tailId).equals("B")) {
					edgesB.add(new Tuple<>(tailId, headId));

				} else {
					edgesMixed.add(new Tuple<>(tailId, headId));
				}
			}
		}

		edges=new Table<>();
		for(Tuple<Long,Long> t : edgesA)
		{
		edges.add(t);
		}
		for(Tuple<Long,Long> t : edgesB)
		{
		edges.add(t);
		}
		for(Tuple<Long,Long> t : edgesMixed)
		{
		edges.add(t);
		}

		//Collections.shuffle(edges);
		MatchingCommunityTest.run(attributes, edges);
	}

	private static Integer getAttribute(int max) {
		return r.nextInt(max);

	}

	
}
