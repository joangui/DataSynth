package org.dama.datasynth.runtime.spark.operators

import org.apache.spark.sql.Dataset
import org.dama.datasynth.executionplan.ExecutionPlan.EdgeTable
import org.dama.datasynth.runtime.spark.SparkRuntime

import SparkRuntime.spark.implicits._

import scala.util.Random

/**
  * Created by aprat on 20/04/17.
  *
  * Operator that generates an EdgeTable
  */
object EdgeTableOperator {


  /**
    * Generates a spark Dataset that corresponds to a given edge table
    * @param node The execution plan node representing the edge table
    * @return The generated spark Dataset
    */
  def apply(node : EdgeTable) : Dataset[(Long,Long,Long)]= {
    val generator = InstantiateStructureGeneratorOperator( node.structure )
    val size = EvalValueOperator(node.size).asInstanceOf[Long]
    val random : Random = new Random()
    val id : Int = random.nextInt()
    val path : String = s"/tmp/${id}"
    generator.run(size, SparkRuntime.spark.sparkContext.hadoopConfiguration,path)
    val edgesRDD = SparkRuntime.spark.sparkContext.
                                             textFile(path)
                                             .map( s => s.split("\t"))
                                             .map( l => (l(0).toLong, l(1).toLong))
                                             .zipWithIndex().map( { case ((tail,head), id) =>  (id, tail, head)})
    SparkRuntime.spark.createDataset(edgesRDD)
  }

}
