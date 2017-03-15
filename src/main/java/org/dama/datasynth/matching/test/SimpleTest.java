/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dama.datasynth.matching.test;

import org.dama.datasynth.matching.Tuple;
import org.dama.datasynth.matching.graphs.types.Graph;
import org.dama.datasynth.matching.graphs.types.Partition;
import org.dama.datasynth.matching.graphs.GraphReaderFromNodePairFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.dama.datasynth.matching.Table;
import org.dama.datasynth.matching.utils.DistributionStatistics;

/**
 *
 * @author joangui
 */
public class SimpleTest {

	static Random r = new Random(1234567890L);
	static int NUM_ATTRIBUTES = 32;

	static public void main(String[] argv) throws Exception {
		System.out.println("Simple Test Run");
		Table<Long, Integer> attributes = new Table<>();

		GraphReaderFromNodePairFile graphReader = new GraphReaderFromNodePairFile(argv[0], argv[1]);

		Graph g = graphReader.getGraph();
		Partition p = graphReader.getPartitions(g);
//for(NUM_ATTRIBUTES=2;NUM_ATTRIBUTES<100;NUM_ATTRIBUTES+=5)
{
		for (Map.Entry<Integer, Set<Long>> entry : p.entrySet()) {
			Integer partitionId = entry.getKey();
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
			long partitionTail = p.getNodePartition(tailId);
			for (Long headId : entry.getValue()) {
				long partitionHead = p.getNodePartition(headId);
				if (partitionTail == partitionHead) {
					Table<Long, Long> edgeTable = edgesPartition.get(partitionHead);
					if (edgeTable == null) {
						edgeTable = new Table<>();
						edgesPartition.put(partitionHead, edgeTable);
					}
					edgeTable.add(new Tuple<>(tailId, headId));
				} else {
					edgesMixed.add(new Tuple<>(tailId, headId));
				}

			}
		}

		Table<Long, Long> edges = new Table<>();
		for (Map.Entry<Long, Table> entry : edgesPartition.entrySet()) {
			Table<Long, Long> edgesTable = entry.getValue();
			for (Tuple<Long, Long> edge : edgesTable) {
				edges.add(edge);
			}
		}

		for (Tuple<Long, Long> edge : edgesMixed) {
			edges.add(edge);
		}

		File originalGraph = new File("./originalGraph.csv");
		FileWriter writer = new FileWriter(originalGraph);
		for (Tuple<Long, Long> edge : edges) {
			writer.write(edge.getX() + " " + edge.getY() + "\n");
		}
		writer.close();
		File originalAttributes = new File("./originalAttributes.csv");
		writer = new FileWriter(originalAttributes);
		writer.write("Id Value\n");
		for (Tuple<Long, Integer> pair : attributes) {
			writer.write(pair.getX() + " " + pair.getY() + "\n");
		}
		writer.close();

		DistributionStatistics ds = MatchingCommunityTest.run(attributes, edges);
		try{
			double chiSquare = ds.chiSquareTest();
		System.out.println("\nChi-Square: " + chiSquare);

		DistributionStatistics.DMaxStatistics dMaxStatistics = ds.dMaxTest();
		System.out.println("\nDmax: " + dMaxStatistics.dMaxValue);

		String fileDmaxPath = "plots/100K-"+NUM_ATTRIBUTES+".txt";
		printFile(fileDmaxPath,dMaxStatistics.accumulativeExpectedProbValues,dMaxStatistics.accumulativeObservedProbValues);
		}catch(Exception e)
		{
			System.out.println("Configuration no possible.");
		}
		
	}
	}

	private static Integer getAttribute(int max) {
		return r.nextInt(max);

	}

	private static void printFile(String fileDmaxPath, ArrayList<Double> accumulativeExpectedProbValues, ArrayList<Double> accumulativeObservedProbValues) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter printWriter = new PrintWriter(fileDmaxPath, "UTF-8");
		printWriter.println(accumulativeExpectedProbValues.toString().replace("[", "").replaceAll("]", "").replaceAll(", ", ","));
		printWriter.println(accumulativeObservedProbValues.toString().replace("[", "").replaceAll("]", "").replaceAll(", ", ","));
		printWriter.close();
		System.out.println(fileDmaxPath+" created.");
	}

}
