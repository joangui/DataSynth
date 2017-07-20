package org.dama.datasynth.runtime.spark.operators.matching.models.stochastic

import org.apache.spark.sql.DataFrame
import org.dama.datasynth.runtime.spark.operators.matching.GraphPartitioner
import org.dama.datasynth.runtime.spark.operators.matching.utils.Partition
import org.dama.datasynth.runtime.spark.operators.matching.utils.traversals.Traversal

import scala.collection.immutable.HashMap


/**
  * Created by joangui on 18/07/2017.
  */
class StochasticBlockModelPartitioner[T <% Ordered[T]](edgeDataframe: DataFrame, traversal:Traversal,blockModel:StochasticBlockModel[T]) extends GraphPartitioner(edgeDataframe, traversal) {

  val blocks: HashMap[T, Long] = blockModel.getMapping()
  val probabilities: HashMap[(T, T), Double] = blockModel.getProbabilities
  val edgesOriginal: Map[(T, T), Long] = calculateOriginalEdges(blocks,probabilities)
  val partition:Partition = new Partition()
  create()


  def getProbability(probabilities: HashMap[(T, T), Double], value1: T, value2: T): Long = {

    probabilities.get(value1,value2) match
      {
      case Some(probability) => probability.toLong
      case None => throw new RuntimeException(s"This should never happen: Accessing a probability of two values ($value1,$value2) that have never been calculated.")
    }

  }

  def calculateOriginalEdges(blocks: HashMap[T, Long], probabilities: HashMap[(T, T), Double]): _root_.scala.Predef.Map[(T, T), Long] = {
    blocks.foldLeft(HashMap[(T,T),Long]())({case(edgesOriginal,(value1,count1))=>{
      blocks.foreach({case (value2,count2)=>
      {
        case 1 if value1 == value2 => {
          val probability: Long = getProbability(probabilities, value1, value2)
          edgesOriginal += ((value1, value2) -> probability * (count1 * (count2 - 1)) / 2)
        }
        case 2 if (value1 < value2) => {
          val probability:Long = getProbability(probabilities,value1,value2)
          edgesOriginal + ((value1,value2) -> probability*count1*count2)
        }
      }})
      edgesOriginal
    }})
  }

  def create(): Unit = {

    //while(traversal.hasNext())

      traversal.foreach(node =>{
        //val node: Long = traversal.next()
        val partitionId: Int = findBestPartition(node)
        partition.addTo(node,partitionId)
      })


  }
}
