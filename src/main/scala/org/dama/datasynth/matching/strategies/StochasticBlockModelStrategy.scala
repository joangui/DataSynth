package org.dama.datasynth.matching.strategies

import org.dama.datasynth.executionplan.ExecutionPlan
import org.dama.datasynth.matching
import org.dama.datasynth.matching.MatchingStrategy

/**
  * Created by joangui on 10/07/2017.
  */
class StochasticBlockModelStrategy extends MatchingStrategy {
  override def run(edgeTable: ExecutionPlan.EdgeTable, propertyTable: ExecutionPlan.PropertyTable[_], jointDistribution: matching.JointDistribution[_, _]): ExecutionPlan.EdgeTable = ???
}
