package org.dama.datasynth

import org.apache.spark.sql.SparkSession
import org.dama.datasynth.executionplan.ExecutionPlan
import org.dama.datasynth.executionplan.ExecutionPlan.{EdgeTable, PropertyTable}
import org.dama.datasynth.lang.ReadExecutionPlan
import org.dama.datasynth.runtime.spark.SparkRuntime
import org.dama.datasynth.runtime.spark.utils.DataSynthConfig

import scala.io.Source

/**
  * Created by aprat on 6/04/17.
  */
object DataSynth {

  def main( args : Array[String] ) {
    val dataSynthConfig = DataSynthConfig(args.toList)
    val json : String = Source.fromFile(dataSynthConfig.schemaFile).getLines.mkString
    val schema = ReadExecutionPlan.loadSchema(json)
    val executionPlan = ReadExecutionPlan.createExecutionPlan(schema)

    SparkRuntime.run(dataSynthConfig, executionPlan)
  }

}
