package org.dama.datasynth.matching.utils

import scala.collection.mutable

/**
  * Created by joangui on 10/07/2017.
  */

object StochasticBlockModel
{
  def apply[T](probabilities:Array[Array[Double]],sizes:Array[Long],mapping:mutable.HashMap[T,Int]): StochasticBlockModel[T] ={
    if (probabilities.length != sizes.length)
      throw new RuntimeException("The stochastic block model is wrong, sizes vector and probabilities matrix side missmatch.")

    mapping.values.foreach(
      {case value => if(value>sizes.length)
        throw new RuntimeException("The stochastic block model is wrong, mapping between values and indices exceeds range.")
      })

    new StochasticBlockModel(probabilities,sizes,mapping)


  }
}


  class StochasticBlockModel[T] private (probabilities:Array[Array[Double]],sizes:Array[Long],mapping:mutable.HashMap[T,Int]){

  def getNumBlocks():Int = sizes.length

  def getSize(value:T):Long = {
    val index:Int=mapping.get(value).getOrElse(-1)
    if (index == -1)
      throw new RuntimeException("The stochastic block model is wrong. THIS SHOULD NEVER HAPPEN DUE TO PREVIOUS CHECKS.")
    sizes(index)
  }
    def getProbability(i:T,j:T):Double={
      val mapI:Int = mapping.get(i).getOrElse(-1)
      val mapJ:Int = mapping.get(j).getOrElse(-1)

       if (mapI < mapJ)
         probabilities(mapI)(mapJ)
       else
         probabilities(mapJ)(mapI)
    }

    def getMapping():Array[Array[Double]] = probabilities
    def getSizes():Array[Long] = sizes
}
