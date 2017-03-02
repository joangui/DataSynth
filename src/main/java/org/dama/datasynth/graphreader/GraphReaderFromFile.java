/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dama.datasynth.graphreader;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dama.datasynth.graphreader.types.Graph;
import org.dama.datasynth.graphreader.types.Partition;

/**
 *
 * @author joangui
 */
public class GraphReaderFromFile implements GraphReaderInterface {

	private final String pathGraph;
	private final String pathPartition;

	public GraphReaderFromFile(String pathGraph, String pathPartition) {
		this.pathGraph = pathGraph;
		this.pathPartition = pathPartition;
	}

	@Override
	public Graph getGraph() throws Exception {
		FileInputStream fstream = null;
		fstream = new FileInputStream(pathGraph);

		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		Graph g = new Graph();
		long nodeId = 1;
		while ((line = br.readLine()) != null) {
			String[] lineArray = line.trim().split(" ");
			Set adjacencies = new HashSet<>();
			for (String node2Id : lineArray) {
				adjacencies.add(new Long(node2Id));
			}

			g.adjacencyList(nodeId, adjacencies);
			nodeId++;
		}
		return g;
	}

	@Override
	public Partition getPartitions(Graph g) throws Exception {
		FileInputStream fstream = null;
		fstream = new FileInputStream(pathPartition);

		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		Partition p = new Partition();
		long nodeId = 1;
		while ((line = br.readLine()) != null) {
			Long partitionId = Long.valueOf(line);
			p.addToPartition(nodeId, partitionId);
			nodeId++;
		}

		return p;
	}

}
