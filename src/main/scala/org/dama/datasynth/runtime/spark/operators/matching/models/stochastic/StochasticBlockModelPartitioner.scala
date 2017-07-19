package org.dama.datasynth.runtime.spark.operators.matching.models.stochastic

import org.apache.spark.sql.DataFrame
import org.dama.datasynth.runtime.spark.operators.matching.GraphPartitioner
import org.dama.datasynth.runtime.spark.operators.matching.utils.traversals.Traversal


/**
  * Created by joangui on 18/07/2017.
  */
class StochasticBlockModelPartitioner[T](edgeDataframe: DataFrame, traversal:Traversal,blockModel:StochasticBlockModel[T]) extends GraphPartitioner(edgeDataframe, traversal) {
}
