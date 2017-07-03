package org.dama.datasynth

import org.apache.spark.sql.SparkSession
import org.dama.datasynth.executionplan.ExecutionPlan
import org.dama.datasynth.executionplan.ExecutionPlan.{EdgeTable, PropertyTable, Table}
import org.dama.datasynth.lang.ReadExecutionPlan
import org.dama.datasynth.runtime.spark.SparkRuntime
import org.dama.datasynth.schema.Schema

import scala.io.Source

/**
  * Created by aprat on 6/04/17.
  */
object DataSynth {

  def main( args : Array[String] ) {
    val dataSynthConfig = DataSynthConfig(args.toList)
    val json : String = Source.fromFile(dataSynthConfig.schemaFile).getLines.mkString
    val schema:Schema = ReadExecutionPlan.loadSchema(json)
    val executionPlan:Seq[Table] = ReadExecutionPlan.createExecutionPlan(schema)

    //SparkSession.builder().master("local[*]").getOrCreate()
    val sparkRuntime = new SparkRuntime(dataSynthConfig)
    sparkRuntime.run(executionPlan)
  }

}
