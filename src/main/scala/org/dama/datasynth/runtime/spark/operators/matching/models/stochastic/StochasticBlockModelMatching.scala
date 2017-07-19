package org.dama.datasynth.runtime.spark.operators.matching.models.stochastic

import org.apache.spark.sql.{DataFrame, Dataset}
import org.dama.datasynth.executionplan.ExecutionPlan
import org.dama.datasynth.runtime.spark.SparkRuntime
import org.dama.datasynth.runtime.spark.operators.matching.MatchingOperator
import org.dama.datasynth.runtime.spark.operators.matching.utils.JointDistribution

/**
  * Created by joangui on 10/07/2017.
  */
class StochasticBlockModelMatching extends MatchingOperator {
//  def run[T](edgeTable: ExecutionPlan.EdgeTable, propertyTable: ExecutionPlan.PropertyTable[T], jointDistribution: matching.JointDistribution[T,T]): ExecutionPlan.EdgeTable =
//  {
//    val caca: ExecutionPlan.ExecutionPlanNode = edgeTable.size
//    caca.ac
//    StochasticBlockModel.extractForm(propertyTable,jointDistribution)
//  }

  override def run[T](node: ExecutionPlan.MatchNode[T]): ExecutionPlan.EdgeTable = {
    val jointDistribution:JointDistribution[T,T] = node.jointDistribution
    val propertyTable = node.propertyTable
    val edgeTable = node.edgeTable

    val numEdges : Long =    SparkRuntime.evalValueOperator(edgeTable.size).asInstanceOf[Long]/2
    val model : StochasticBlockModel[T] = StochasticBlockModel.extractForm(numEdges,propertyTable,jointDistribution)
    val edgeDataset: Dataset[_] = SparkRuntime.fetchTableOperator(edgeTable)
    val edgeDataframe: DataFrame= edgeDataset.toDF("id","source","target")



    _
  }

  override def run[T1,T2](node: ExecutionPlan.BipartiteMatchNode[T1,T2]): ExecutionPlan.EdgeTable = {

    _
  }
}
