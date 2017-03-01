package org.dama.datasynth.utils

/**
  * Created by aprat on 21/12/16.
  */
class Similarity ( samples : List[(Int,Int)]) {

  val arraySize = samples.flatMap( a => List(a._1, a._2)).fold(0) {
    (a,b) => {
      Math.max(a, b)
    }
  } + 1

  val occurrences  = Array.fill[Int](arraySize)(0)
  samples.map( entry => {
    occurrences(entry._1)+=1
    occurrences(entry._2)+=1
  })

  var cooccurrences = Array.fill[Int](arraySize,arraySize)(0)
  samples.map( a => {
    a._1 < a._2 match {
      case true => cooccurrences(a._1)(a._2)+=1
      case false => cooccurrences(a._2)(a._1)+=1
    }
  })

  def apply( a : Int, b : Int): Double = {
    val denom : Double = occurrences(a) + occurrences(b)
      denom > 0 match {
        case true =>
          a < b match {
            case true => 2*cooccurrences(a)(b)/denom
            case false => 2*cooccurrences(b)(a)/denom
          }
        case false => 0.0
      }
  }

}
