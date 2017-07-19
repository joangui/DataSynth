/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dama.datasynth.runtime.spark.operators.matching.graphs;

import org.dama.datasynth.runtime.spark.operators.matching.graphs.types.Graph;
import org.dama.datasynth.runtime.spark.operators.matching.graphs.types.Partition;

/**
 *
 * @author joangui
 */
public interface GraphReaderInterface {

	public Graph getGraph() throws Exception;
	public Partition getPartitions(Graph g) throws Exception;
}
