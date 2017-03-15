/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dama.datasynth.matching.test;

import org.dama.datasynth.matching.Table;
import org.dama.datasynth.matching.Tuple;
import org.dama.datasynth.matching.graphs.BFSTraversal;
import org.dama.datasynth.matching.graphs.GraphReaderFromNodePairFile;
import org.dama.datasynth.matching.graphs.LinearWeightedGreedyPartitioner;
import org.dama.datasynth.matching.graphs.types.Graph;
import org.dama.datasynth.matching.graphs.types.GraphPartitioner;
import org.dama.datasynth.matching.graphs.types.Partition;
import org.dama.datasynth.matching.utils.DistributionStatistics;

import java.io.*;
import java.util.*;

/**
 *
 * @author joangui
 */
public class UnbalancedPartitionsTest {

	static Random r = new Random(1234567890L);
	static int NUM_ATTRIBUTES = 16;
	static double k = 0.3;

	static public void main(String[] argv) throws Exception {
		System.out.println("Unbalanced Partition Test");
		Table<Long, Integer> attributes = new Table<>();

		Table<Long, Long> edges = new Table<>();
		// Load edges table
		try {
			FileInputStream	fileInputStream = new FileInputStream(argv[0]);
			edges.load(fileInputStream, " ", (String s) -> Long.parseLong(s), (String s) -> Long.parseLong(s));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		double proportions[] = new double[NUM_ATTRIBUTES];
		double sum = 0.0;
		for(int i = 1; i <= NUM_ATTRIBUTES; ++i) {
			double prob = Math.max(Math.pow(1-k,i-1)*k,1/(double)NUM_ATTRIBUTES);

			sum+=prob;
			proportions[i-1] = prob;
		}

		for(int i = 0; i < NUM_ATTRIBUTES; ++i) {
			proportions[i] = proportions[i] / sum;
		}

		Graph graph = Graph.fromTable(edges);
		GraphPartitioner partitioner = new LinearWeightedGreedyPartitioner(graph, BFSTraversal.class, proportions );
		Partition p = partitioner.getPartition();
		for (Map.Entry<Integer, Set<Long>> entry : p.entrySet()) {
			Integer partitionId = entry.getKey();
			Set<Long> nodes = entry.getValue();

			Integer attribute = partitionId % NUM_ATTRIBUTES;

			for (long nodeId : nodes) {
				attributes.add(new Tuple<>(nodeId, attribute));
			}
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

		String fileDmaxPath = "./plots/100K-"+NUM_ATTRIBUTES+".txt";
		printFile(fileDmaxPath,dMaxStatistics.accumulativeExpectedProbValues,dMaxStatistics.accumulativeObservedProbValues);
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Configuration no possible.");
		}
	}

	/*private static Integer getAttribute(int max) {
		return r.nextInt(max);

	}*/

	private static void printFile(String fileDmaxPath, ArrayList<Double> accumulativeExpectedProbValues, ArrayList<Double> accumulativeObservedProbValues) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter printWriter = new PrintWriter(fileDmaxPath, "UTF-8");
		printWriter.println(accumulativeExpectedProbValues.toString().replace("[", "").replaceAll("]", "").replaceAll(", ", ","));
		printWriter.println(accumulativeObservedProbValues.toString().replace("[", "").replaceAll("]", "").replaceAll(", ", ","));
		printWriter.close();
		System.out.println(fileDmaxPath+" created.");
	}

}
