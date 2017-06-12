package org.dama.datasynth.runtime.spark.operators

import org.apache.spark.sql.{Dataset, Encoder, SparkSession}
import org.dama.datasynth.executionplan.ExecutionPlan.PropertyTable

/**
  * Created by aprat on 9/04/17.
  */
object PropertyTableOperator {


  /**
    * Creates an Spark Dataset representing a boolean property table
    * @param sparkSession The session this operator works for
    * @param node The execution plan node representing this operation
    * @return The created Dataset
    */
  def boolean(sparkSession : SparkSession)( node : PropertyTable[Boolean] ) : Dataset[(Long,Boolean)] = {
    import sparkSession.implicits._
    val generator = InstantiatePropertyGeneratorOperator[Boolean](sparkSession, node.name, node.generator)
    val size : Long = EvalValueOperator(sparkSession,node.size).asInstanceOf[Long]
    sparkSession.range (0, size).map (i => Tuple2[Long,Boolean](i.toLong, generator(i) ) )
  }

  /**
    * Creates an Spark Dataset representing an int property table
    * @param sparkSession The session this operator works for
    * @param node The execution plan node representing this operation
    * @return The created Dataset
    */
  def int(sparkSession : SparkSession)(node : PropertyTable[Int] ) : Dataset[(Long,Int)] = {
    import sparkSession.implicits._
    val generator = InstantiatePropertyGeneratorOperator[Int](sparkSession, node.name, node.generator)
    val size : Long = EvalValueOperator(sparkSession, node.size).asInstanceOf[Long]
    sparkSession.range (0, size).map (i => Tuple2[Long,Int](i.toLong, generator(i) ) )
  }

  /**
    * Creates an Spark Dataset representing a long property table
    * @param sparkSession The session this operator works for
    * @param node The execution plan node representing this operation
    * @return The created Dataset
    */
  def long(sparkSession : SparkSession)(node : PropertyTable[Long] ) : Dataset[(Long,Long)] = {
    import sparkSession.implicits._
    val generator = InstantiatePropertyGeneratorOperator[Long](sparkSession,node.name, node.generator)
    val size : Long = EvalValueOperator(sparkSession, node.size).asInstanceOf[Long]
    sparkSession.range (0, size).map (i => Tuple2[Long,Long](i.toLong, generator(i) ) )
  }

  /**
    * Creates an Spark Dataset representing a float property table
    * @param sparkSession The session this operator works for
    * @param node The execution plan node representing this operation
    * @return The created Dataset
    */
  def float(sparkSession : SparkSession)(node : PropertyTable[Float] ) : Dataset[(Long,Float)] = {
    import sparkSession.implicits._
    val generator = InstantiatePropertyGeneratorOperator[Float](sparkSession, node.name, node.generator)
    val size : Long = EvalValueOperator(sparkSession, node.size).asInstanceOf[Long]
    sparkSession.range (0, size).map (i => Tuple2[Long,Float](i.toLong, generator(i) ) )
  }

  /**
    * Creates an Spark Dataset representing a double property table
    * @param sparkSession The session this operator works for
    * @param node The execution plan node representing this operation
    * @return The created Dataset
    */
  def double(sparkSession : SparkSession)(node : PropertyTable[Double] ) : Dataset[(Long,Double)] = {
    import sparkSession.implicits._
    val generator = InstantiatePropertyGeneratorOperator[Double](sparkSession, node.name, node.generator)
    val size : Long = EvalValueOperator(sparkSession, node.size).asInstanceOf[Long]
    sparkSession.range (0, size).map (i => Tuple2[Long,Double](i.toLong, generator(i) ) )
  }

  /**
    * Creates an Spark Dataset representing a string property table
    * @param sparkSession The session this operator works for
    * @param node The execution plan node representing this operation
    * @return The created Dataset
    */
  def string(sparkSession : SparkSession)(node : PropertyTable[String] ) : Dataset[(Long,String)] = {
    import sparkSession.implicits._
    val generator = InstantiatePropertyGeneratorOperator[String](sparkSession, node.name, node.generator)
    val size : Long = EvalValueOperator(sparkSession, node.size).asInstanceOf[Long]
    sparkSession.range (0, size).map (i => Tuple2[Long,String](i.toLong, generator(i) ) )
  }
}
