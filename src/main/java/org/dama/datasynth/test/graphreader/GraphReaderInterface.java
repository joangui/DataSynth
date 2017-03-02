/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dama.datasynth.test.graphreader;

import org.dama.datasynth.test.graphreader.types.Partition;
import org.dama.datasynth.test.graphreader.types.Graph;

/**
 *
 * @author joangui
 */
public interface GraphReaderInterface {

	public Graph getGraph() throws Exception;
	public Partition getPartitions(Graph g) throws Exception;
}
