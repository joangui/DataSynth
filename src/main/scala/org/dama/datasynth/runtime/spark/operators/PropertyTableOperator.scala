package org.dama.datasynth.runtime.spark.operators

import org.apache.spark.sql.{Dataset, Encoder, SparkSession}
import org.dama.datasynth.executionplan.ExecutionPlan.PropertyTable
import org.dama.datasynth.runtime.spark.SparkRuntime

/**
  * Created by aprat on 9/04/17.
  */
class PropertyTableOperator {


  /**
    * Creates an Spark Dataset representing a boolean property table
    * @param node The execution plan node representing this operation
    * @return The created Dataset
    */
  def boolean( node : PropertyTable[Boolean] ) : Dataset[(Long,Boolean)] = {
    val sparkSession = SparkRuntime.getSparkSession()
    import sparkSession.implicits._
    val generator = SparkRuntime.instantiatePropertyGeneratorOperator[Boolean](node.name, node.generator)
    val size : Long = SparkRuntime.evalValueOperator(node.size).asInstanceOf[Long]
    sparkSession.range (0, size)
                .map (i => Tuple2[Long,Boolean](i.toLong, generator(i) ) )
  }

  /**
    * Creates an Spark Dataset representing an int property table
    * @param node The execution plan node representing this operation
    * @return The created Dataset
    */
  def int(node : PropertyTable[Int] ) : Dataset[(Long,Int)] = {
    val sparkSession = SparkRuntime.getSparkSession()
    import sparkSession.implicits._
    val generator = SparkRuntime.instantiatePropertyGeneratorOperator[Int](node.name, node.generator)
    val size : Long = SparkRuntime.evalValueOperator( node.size).asInstanceOf[Long]
    sparkSession.range (0, size)
                .map (i => Tuple2[Long,Int](i.toLong, generator(i) ) )
  }

  /**
    * Creates an Spark Dataset representing a long property table
    * @param node The execution plan node representing this operation
    * @return The created Dataset
    */
  def long(node : PropertyTable[Long] ) : Dataset[(Long,Long)] = {
    val sparkSession = SparkRuntime.getSparkSession()
    import sparkSession.implicits._
    val generator = SparkRuntime.instantiatePropertyGeneratorOperator[Long](node.name, node.generator)
    val size : Long = SparkRuntime.evalValueOperator(node.size).asInstanceOf[Long]
    sparkSession.range (0, size)
                .map (i => Tuple2[Long,Long](i.toLong, generator(i) ) )
  }

  /**
    * Creates an Spark Dataset representing a float property table
    * @param node The execution plan node representing this operation
    * @return The created Dataset
    */
  def float(node : PropertyTable[Float] ) : Dataset[(Long,Float)] = {
    val sparkSession = SparkRuntime.getSparkSession()
    import sparkSession.implicits._
    val generator = SparkRuntime.instantiatePropertyGeneratorOperator[Float](node.name, node.generator)
    val size : Long = SparkRuntime.evalValueOperator(node.size).asInstanceOf[Long]
    sparkSession.range (0, size)
                .map (i => Tuple2[Long,Float](i.toLong, generator(i) ) )
  }

  /**
    * Creates an Spark Dataset representing a double property table
    * @param node The execution plan node representing this operation
    * @return The created Dataset
    */
  def double(node : PropertyTable[Double] ) : Dataset[(Long,Double)] = {
    val sparkSession = SparkRuntime.getSparkSession()
    import sparkSession.implicits._
    val generator = SparkRuntime.instantiatePropertyGeneratorOperator[Double](node.name, node.generator)
    val size : Long = SparkRuntime.evalValueOperator( node.size).asInstanceOf[Long]
    sparkSession.range (0, size)
                .map (i => Tuple2[Long,Double](i.toLong, generator(i) ) )
  }

  /**
    * Creates an Spark Dataset representing a string property table
    * @param node The execution plan node representing this operation
    * @return The created Dataset
    */
  def string(node : PropertyTable[String] ) : Dataset[(Long,String)] = {
    val sparkSession = SparkRuntime.getSparkSession()
    import sparkSession.implicits._
    val generator = SparkRuntime.instantiatePropertyGeneratorOperator[String](node.name, node.generator)
    val size : Long = SparkRuntime.evalValueOperator(node.size).asInstanceOf[Long]
    sparkSession.range (0, size)
                .map (i => Tuple2[Long,String](i.toLong, generator(i) ) )
  }
}
