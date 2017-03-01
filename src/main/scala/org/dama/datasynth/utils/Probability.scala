package org.dama.datasynth.utils

import scala.collection.mutable

/**
  * Created by aprat on 30/12/16.
  */

object Probability {
  def conditionalProbability(samples: List[(Int, Int)]): mutable.Map[Int, Array[Double]] = {

    val arraySize = samples.flatMap(a => List(a._1, a._2)).fold(0) {
      (a, b) => {
        Math.max(a, b)
      }
    } + 1

    var counts = new mutable.HashMap[Int, Array[Double]]()

    samples.foreach(pair => {
      if(!counts.contains(pair._1)) counts.put(pair._1, Array.fill[Double](arraySize)(0))
      if(!counts.contains(pair._2)) counts.put(pair._2, Array.fill[Double](arraySize)(0))
      counts(pair._1)(pair._2) += 1
      counts(pair._2)(pair._1) += 1
    })
    var sums: mutable.Map[Int, Double] = counts.map(pair => (pair._1, pair._2.foldLeft(0.0)((a, b) => a + b)))
    sums.map(pair => (pair._1, counts(pair._1).map(x => x / pair._2)))
  }
}
