package org.dama.datasynth.utils

import no.uib.cipr.matrix.{DenseVector, Matrix, Vector}
import org.dama.datasynth.algorithms.HungarianAlgorithm

/**
  * Created by aprat on 17/11/16.
  */
object Algorithms {

  def extractRow( matrix : Matrix, row : Int) : Vector = {
    new DenseVector(Range(0,matrix.numColumns).map(column => matrix.get(row,column)).toArray)
  }

  def findMatching( n : Int, m : Int, distance : ( Int, Int) => Double): Array[Int] = {

    println("Building cost matrix")
    var start = System.nanoTime
    val costMatrix = Array.ofDim[Double](n,m)
    Range(0,n).foreach( i =>
      Range(0,m).foreach( j =>
        costMatrix(i)(j) = distance(i,j)
      )
    )
    println("Cost matrix built in "+(System.nanoTime - start)/1000000 + " ms")
    println("Running algorithm")
    start = System.nanoTime
    var hungarianAlgorithm : HungarianAlgorithm = new HungarianAlgorithm(costMatrix)
    val ret = hungarianAlgorithm.execute()
    println("Algorithm run in in "+(System.nanoTime - start)/1000000 + " ms")
    ret
  }

}
