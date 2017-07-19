package org.dama.datasynth.runtime.spark.operators.matching.utils

import org.apache.spark.sql.{DataFrame, Row}

import scala.collection.mutable

/**
  * Created by joangui on 19/07/2017.
  */
public class Graph(edgeDataFrame:DataFrame) extends mutable.HashMap[Long,mutable.Set[Long]] {
  addEdges()

  def addEdges(): Unit = {
    val edges: Array[(Long, Long)] = edgeDataFrame.select("source", "target").collect().
      map { case Row(source: Long, target: Long) => (source, target) }

    edges.foreach({ case (source, target) =>
      this.get(source) match {
        case Some(set) => set.add(target)
          this.put(source, set)
        case None =>
          val set: mutable.Set[Long] = new mutable.HashSet[Long]()
          set.add(target)
          this.put(source, set)
      }

      this.get(target) match {
        case Some(set) => set.add(source)
          this.put(target, set)
        case None =>
          val set: mutable.Set[Long] = new mutable.HashSet[Long]()
          set.add(source)
          this.put(target, set)
      }
    })
  }

def getNeighbors(node:Long) : mutable.Set[Long]=
  this.get(node) match
    {
    case Some(set) => set
    case None => new mutable.HashSet[Long]()
  }
}
