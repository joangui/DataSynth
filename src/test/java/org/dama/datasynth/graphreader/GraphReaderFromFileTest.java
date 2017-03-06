/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dama.datasynth.graphreader;

import org.dama.datasynth.test.graphreader.GraphReaderFromFile;
import org.dama.datasynth.test.graphreader.types.Graph;
import org.dama.datasynth.test.graphreader.types.Partition;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author joangui
 */
public class GraphReaderFromFileTest {
	
	@Test
	public void testPartitioner() throws Exception {
		String rootPath ="/Users/joangui/DAMA/DataSynth/parmetis-4.0.3/Graphs/";
		GraphReaderFromFile graphReaderFromFile = new GraphReaderFromFile(rootPath+"rotor.graph", rootPath+"rotor.graph.part");
		Graph g =graphReaderFromFile.getGraph();
		long numNodes = g.numNodes();
		assertTrue(numNodes==99618L);
		
		Partition partitions = graphReaderFromFile.getPartitions(g);
		assertTrue(partitions.numPartitions()==25L);

		assertTrue(partitions.numNodes()==99617L);
		
	}
}