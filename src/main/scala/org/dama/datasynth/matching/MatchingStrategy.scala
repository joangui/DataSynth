package org.dama.datasynth.matching

import org.dama.datasynth.executionplan.ExecutionPlan.{EdgeTable, PropertyTable}

/**
  * Created by joangui on 10/07/2017.
  */
trait MatchingStrategy {

  def run(edgeTable : EdgeTable, propertyTable : PropertyTable[_], jointDistribution: JointDistribution[_,_]):EdgeTable
}
