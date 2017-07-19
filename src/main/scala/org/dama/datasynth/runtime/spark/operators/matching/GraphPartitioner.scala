package org.dama.datasynth.runtime.spark.operators.matching


import org.apache.spark.sql.DataFrame
import org.dama.datasynth.executionplan.ExecutionPlan.EdgeTable
import org.dama.datasynth.runtime.spark.operators.matching.utils.traversals.Traversal
import org.dama.datasynth.runtime.spark.operators.matching.utils.Graph
/**
  * Created by joangui on 17/07/2017.
  */
abstract class GraphPartitioner(edgeDataframe: DataFrame, traversal:Traversal) {
  val graph:Graph = new Graph(edgeDataframe)
  traversal
}
