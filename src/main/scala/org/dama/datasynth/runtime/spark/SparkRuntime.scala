package org.dama.datasynth.runtime.spark

import org.apache.spark.sql.{Dataset, SparkSession}
import org.dama.datasynth.executionplan.ExecutionPlan
import org.dama.datasynth.runtime.spark.operators.FetchTableOperator
import org.dama.datasynth.runtime.spark.utils.DataSynthConfig

/**
  * Created by aprat on 6/04/17.
  */
object SparkRuntime {

  val spark = SparkSession
    .builder()
    .getOrCreate()

  def run( c : DataSynthConfig, executionPlan : Seq[ExecutionPlan.Table] ) = {
    executionPlan.foreach(x =>
          FetchTableOperator(x).write.csv(c.outputDir+"/"+x.name)
    )
  }
}

