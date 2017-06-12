package org.dama.datasynth.runtime.spark.operators

import org.apache.spark.sql.SparkSession
import org.dama.datasynth.executionplan.ExecutionPlan.{StaticValue, TableSize, Value}

/**
  * Created by aprat on 9/04/17.
  *
  * Operator that evaluates the value of a Value execution plan node
  */
object EvalValueOperator {

  /** Evaluates the value of the given Value and returns its result as Any
    *
    * @param sparkSession The session this operator works for
    * @param node The execution plan node representing the Value to evaluate
    * @return The actual value of the Value as an Any object
    */
  def apply( sparkSession : SparkSession, node : Value[_]) : Any = {
    node match {
      case v : StaticValue[_] => v.value
      case v : TableSize => TableSizeOperator(sparkSession,v)
    }
  }
}
