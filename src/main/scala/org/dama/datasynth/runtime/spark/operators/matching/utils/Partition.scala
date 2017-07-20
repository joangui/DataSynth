package org.dama.datasynth.runtime.spark.operators.matching.utils

import scala.collection.mutable

class Partition extends mutable.HashMap[Int,mutable.Set[Long]] {
  this = new mutable.HashMap[Int,mutable.Set[Long]]()
  val partitionDictionary:mutable.HashMap[Long,Integer] = new mutable.HashMap[Long,Integer]()

  def addTo(nodeId: Long, partitionId : Int): Unit =
  {
    get(partitionId) match
    {
      case Some(partition) => {
        partition.add(nodeId)
      }
      case None => {
        val partition:mutable.HashSet[Long] = new mutable.HashSet[Long]()
        partition.add(partitionId)
        put(partitionId,partition)
      }
    }
    partitionDictionary.put(nodeId,partitionId)
  }

  def getNode(nodeId:Long): Option[Integer] = {
    partitionDictionary.get(nodeId)
  }

  def getPartitionSize (partitionId: Int):Int = {
    get(partitionId) match
    {
      case Some(partition) => partition.size
      case None => 0
    }
  }


}
