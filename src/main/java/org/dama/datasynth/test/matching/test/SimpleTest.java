/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dama.datasynth.test.matching.test;

import java.util.Collections;
import java.util.Map;
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

	static public void main(String[] argv) throws Exception {
		System.out.println("Simple Test Run");
		Table<Long, String> attributes = null;
		Table<Long, Long> edges = null;

		GraphReaderFromNodePairFile graphReader = new GraphReaderFromNodePairFile(argv[0], argv[1]);

		Graph g = graphReader.getGraph();
		Partition p = graphReader.getPartitions(g);

		attributes = new Table<>();
		for (Map.Entry<Long, Set<Long>> entry : p.partitions().entrySet()) {
			Long partitionId = entry.getKey();
			Set<Long> nodes = entry.getValue();

			String attribute = partitionId % 2 == 0 ? "A" : "B";

			for (long nodeId : nodes) {
				attributes.add(new Tuple<>(nodeId, attribute));
			}
		}

		Dictionary<Long, String> dictonariAttributes = new Dictionary<>(attributes);

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

		Collections.shuffle(edges);
		MatchingCommunityTest.run(attributes, edges);
	}
}
