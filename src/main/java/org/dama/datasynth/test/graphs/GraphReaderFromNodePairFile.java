/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dama.datasynth.test.graphs;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.dama.datasynth.test.graphs.types.Graph;
import org.dama.datasynth.test.graphs.types.Partition;

/**
 *
 * @author joangui
 */
public class GraphReaderFromNodePairFile implements GraphReaderInterface {

	private final String pathGraph;
	private final String pathPartition;

	public GraphReaderFromNodePairFile(String pathGraph, String pathPartition) {
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
		Long nodeIdTail = null;
		Long nodeIdHead = null;
		while ((line = br.readLine()) != null) {
			String[] lineArray = line.trim().split(" ");
			nodeIdTail = Long.valueOf(lineArray[0]);
			nodeIdHead = Long.valueOf(lineArray[1]);

			g.addEdge(nodeIdTail, nodeIdHead);
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
		Long nodeId;
		Long partitionId;
		while ((line = br.readLine()) != null) {
			String[] lineArray = line.split(" ");
			nodeId = Long.valueOf(lineArray[0]);
			partitionId = Long.valueOf(lineArray[1]);
			p.addToPartition(nodeId, partitionId);
			nodeId++;
		}

		return p;
	}

}
