package org.dama.datasynth.runtime.spark.operators

import org.apache.spark.sql.{Dataset, SparkSession}
import org.dama.datasynth.executionplan.ExecutionPlan.TableSize
import org.dama.datasynth.runtime.spark.SparkRuntime

/**
  * Created by aprat on 9/04/17.
  *
  * Oeprator that evaluates the size of a table
  */
 class TableSizeOperator {

  /**
    * Operator that execute the TableSize operation
    * @param node The execution plan node representing the table size operation
    * @return The size of the table
    */
  def apply( node : TableSize ) : Long = {

    SparkRuntime.fetchTableOperator(node.table).count
  }

}
