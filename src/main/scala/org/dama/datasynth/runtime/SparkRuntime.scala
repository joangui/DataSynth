package org.dama.datasynth.runtime

import org.apache.spark.sql.{Dataset, SparkSession}
import org.dama.datasynth.executionplan.ExecutionPlan._
import org.dama.datasynth.runtime.operators.{PropertyTableOperator, FetchTableOperator}
import org.dama.datasynth.executionplan.ExecutionPlan.Table
import org.dama.datasynth.executionplan.{ExecutionPlan, ExecutionPlanVoidVisitor}

import scala.collection.mutable

/**
  * Created by aprat on 6/04/17.
  */
object SparkRuntime {

  var booleanTables = new mutable.HashMap[String, Dataset[(Long,Boolean)]]
  var intTables     = new mutable.HashMap[String, Dataset[(Long,Int)]]
  var longTables    = new mutable.HashMap[String, Dataset[(Long,Long)]]
  var floatTables   = new mutable.HashMap[String, Dataset[(Long,Float)]]
  var doubleTables  = new mutable.HashMap[String, Dataset[(Long,Double)]]
  var stringTables  = new mutable.HashMap[String, Dataset[(Long,String )]]

  var edgeTables = new mutable.HashMap[String,Dataset[(Long,Long)]]

  val spark = SparkSession
    .builder()
    .appName("Spark SQL basic example")
    .master("local")
    .config("spark.some.config.option", "some-value")
    .getOrCreate()

  def run( executionPlan : Seq[ExecutionPlan.Table] ) = {
    executionPlan.foreach(x => FetchTableOperator.apply(x).collect())
  }

}
