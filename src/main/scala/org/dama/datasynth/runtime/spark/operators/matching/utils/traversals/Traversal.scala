package org.dama.datasynth.runtime.spark.operators.matching.utils.traversals

import org.dama.datasynth.executionplan.ExecutionPlan.EdgeTable

/**
  * Created by joangui on 18/07/2017.
  */
trait Traversal extends Traversable[Long]{
  def hasNext():Boolean
  def next():Long
}
