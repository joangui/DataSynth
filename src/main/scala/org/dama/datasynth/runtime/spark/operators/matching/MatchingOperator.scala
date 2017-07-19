package org.dama.datasynth.runtime.spark.operators.matching

import org.dama.datasynth.executionplan.ExecutionPlan.{BipartiteMatchNode, EdgeTable, MatchNode}

/**
  * Created by joangui on 10/07/2017.
  */
trait MatchingOperator {
  def run[T](node : MatchNode[T]):EdgeTable
  def run[T1,T2](node : BipartiteMatchNode[T1,T2]):EdgeTable
}
