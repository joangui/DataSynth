/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dama.datasynth.runtime.spark.operators.matching.test;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.dama.datasynth.runtime.spark.operators.matching.Table;
import org.dama.datasynth.runtime.spark.operators.matching.Tuple;
import org.dama.datasynth.runtime.spark.operators.matching.graphs.LinearWeightedGreedyPartitioner;
import org.dama.datasynth.runtime.spark.operators.matching.graphs.RandomTraversal;
import org.dama.datasynth.runtime.spark.operators.matching.graphs.types.Graph;
import org.dama.datasynth.runtime.spark.operators.matching.graphs.types.GraphPartitioner;
import org.dama.datasynth.runtime.spark.operators.matching.graphs.types.Partition;
import org.dama.datasynth.runtime.spark.operators.matching.utils.DistributionStatistics;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author joangui
 */
public class UnbalancedPartitionsTest {

	static Random r = new Random(1234567890L);

	private static class Options {

		@Parameter(names = {"-o","--output"}, description = "Path to the output file", required = true)
		public String outputFile;

		@Parameter(names = {"-i","--input"}, description = "Path to the input file", required = true)
		public String inputFile;


		@Parameter(names = {"-n","--nattributes"}, description = "Number of attributes to generate")
		public int numAttributes = 4;

		@Parameter(names = {"-k","--kparam"}, description = "Value of the k parameter of the proportions assignment distribution")
		public double k = 0.4;

	}


	static public void main(String[] argv) throws Exception {

	    Options options = new Options();
		JCommander commander = new JCommander(options,argv);


		System.out.println("Unbalanced Partition Test");
		Table<Long, Integer> attributes = new Table<>();

		Table<Long, Long> edges = new Table<>();
		// Load edges table
		try {
			FileInputStream	fileInputStream = new FileInputStream(options.inputFile);
			edges.load(fileInputStream, " ", (String s) -> Long.parseLong(s), (String s) -> Long.parseLong(s));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		double proportions[] = new double[options.numAttributes];
		double sum = 0.0;
		for(int i = 1; i <= options.numAttributes; ++i) {
			double prob = Math.max(Math.pow(1-options.k,i-1)*options.k,1/(double)options.numAttributes);
			sum+=prob;
			proportions[i-1] = prob;
		}

		for(int i = 0; i < options.numAttributes; ++i) {
			proportions[i] = proportions[i] / sum;
		}

		Graph graph = Graph.fromTable(edges);
		GraphPartitioner partitioner = new LinearWeightedGreedyPartitioner(graph, RandomTraversal.class, proportions );
		Partition p = partitioner.getPartition();
		for (Map.Entry<Integer, Set<Long>> entry : p.entrySet()) {
			Integer partitionId = entry.getKey();
			Set<Long> nodes = entry.getValue();

			Integer attribute = partitionId % options.numAttributes;

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
		try {
            long numsamples = edges.size();
            double chiSquare = ds.chiSquareTest(numsamples);
            System.out.println("\np-value of chi-square test with " + numsamples + " samples: " + chiSquare);
        }
        catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Configuration no possible.");
		}

        try{
            DistributionStatistics.DMaxStatistics dMaxStatistics = ds.dMaxTest();
            System.out.println("\nDmax: " + dMaxStatistics.dMaxValue);

            printFile(options.outputFile,dMaxStatistics.accumulativeExpectedProbValues,dMaxStatistics.accumulativeObservedProbValues);
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
