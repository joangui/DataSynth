package org.dama.datasynth.runtime.spark.operators.matching.utils.traversals
import org.apache.spark.sql.DataFrame
import org.dama.datasynth.executionplan.ExecutionPlan
import org.dama.datasynth.executionplan.ExecutionPlan.EdgeTable

import scala.collection._
import scala.collection.mutable.ListBuffer
/**
  * Created by joangui on 18/07/2017.
  */
class RandomTraversal(edgeTable:DataFrame) extends Traversal{
  val targetNodes: Array[Long] = edgeTable.select(edgeTable("target")).collect.map(_.getAs[Long]("target"))
  val sourceNodes: Array[Long] = edgeTable.select(edgeTable("source")).collect.map(_.getAs[Long]("source"))
  val nodesTmp: Array[Long] = targetNodes.union(sourceNodes).distinct
  val nodesTmp2: mutable.ListBuffer[Long] = nodesTmp.foldLeft(new mutable.ListBuffer[Long])({case (nodes,nextNode)=>nodes += nextNode})
  val nodes: mutable.ListBuffer[Long] = util.Random.shuffle(nodesTmp2)
  override def hasNext(): Boolean = nodes.nonEmpty
  override def next(): Long = nodes.remove(0)
  def getNodes():ListBuffer[Long] = nodes

  //override def foreach[U](f: (Long) => U): Unit = nodes.foreach()
  override def foreach[U](f: Long => U) = nodes.foreach(f)

}
