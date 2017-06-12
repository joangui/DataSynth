package org.dama.datasynth.runtime.spark.operators

import org.apache.spark.sql.{Dataset, SparkSession}
import org.dama.datasynth.executionplan.ExecutionPlan.EdgeTable
import org.dama.datasynth.runtime.spark.SparkRuntime

import scala.util.Random

/**
  * Created by aprat on 20/04/17.
  *
  * Operator that generates an EdgeTable
  */
object EdgeTableOperator {


  /**
    * Generates a spark Dataset that corresponds to a given edge table
    * @param sparkSession The session this operator works for
    * @param node The execution plan node representing the edge table
    * @return The generated spark Dataset
    */
  def apply(sparkSession : SparkSession, node : EdgeTable) : Dataset[(Long,Long,Long)]= {
    import sparkSession.implicits._
    val generator = InstantiateStructureGeneratorOperator(sparkSession, node.structure )
    val size = EvalValueOperator(sparkSession,node.size).asInstanceOf[Long]
    val random : Random = new Random()
    val id : Int = random.nextInt()
    val path : String = s"/tmp/${id}"
    val sparkContext = sparkSession.sparkContext
    generator.run(size, sparkContext.hadoopConfiguration,path)
    val edgesRDD = sparkContext.textFile(path)
                               .map( s => s.split("\t"))
                               .map( l => (l(0).toLong, l(1).toLong))
                               .zipWithIndex().map( { case ((tail,head), id) =>  (id, tail, head)})
    sparkSession.createDataset(edgesRDD)
  }

}
