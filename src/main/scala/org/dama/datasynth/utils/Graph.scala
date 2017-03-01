package org.dama.datasynth.utils

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * Created by aprat on 29/11/16.
  */
class Graph( n : Int) {
  var matrix : ArrayBuffer[mutable.HashMap[Int,Double]] = ArrayBuffer.fill[mutable.HashMap[Int,Double]](n){mutable.HashMap[Int,Double]()}
  var numEdges = 0;

  def neighbors( i : Int ) : mutable.HashMap[Int,Double] = {
    matrix(i)
  }

  def addNeighbor( i : Int, j : Int, weight : Double ) : Unit = {
    matrix(i).put(j,weight)
    numEdges+=1
  }

  def hasNeighbor( i : Int, j : Int ) : Boolean = {
    matrix(i).keySet.exists( n => n == j)
  }

  def neighborValue( i : Int, j : Int) : Double = {
    matrix(i)(j)
  }

  def size : Int = matrix.length

  def degree : Int = numEdges

  def nodes : Range = Range(0,matrix.length)


}
