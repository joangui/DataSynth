package org.dama.datasynth.common.generators.property.empirical

import org.dama.datasynth.common.generators.property.PropertyGenerator
import org.dama.datasynth.runtime.spark.utils.RndGenerator

import scala.io.Source

/**
  * Created by aprat on 12/05/17.
  *
  * Property generator based on a distribution file. The file has two columns [value,probability],
  * where probability is the marginal probability of observing the given value
  * Probabilities of the second column must add 1 or be very close.
  */
class DistributionBasedGenerator[T]( parser : (String) => T)  extends PropertyGenerator[T] {

  var data : Array[(T,Double)] = Array[(T,Double)]()

  /**
    * Performs a binary search over the given array containing pairs value-probability, where probability is the
    * cumulative probability of observing such value. Thus, the array must be sorted increasingly by the probability.
    * The method returns the value whose probability is closer to the given probability, which has been obtained
    * uniformly. This method is widely known as the inverse sampling method.
    * @param data The array with pairs of value-probability
    * @param prob
    * @return
    */
  def binarySearchForValue(data : Array[(T,Double)], prob : Double) : T = {
    def doSearch(data : Array[(T,Double)]) : T = {
      if (data.size == 1) {
        data(0) match { case (value,prob) => value }
      } else {
        val middleIndex = data.size / 2
        val (left,right) = data.splitAt(middleIndex)
        (data(middleIndex-1), data(middleIndex)) match {
          case ((_,_),(value, valProb)) if valProb == prob => value
          case ((_,_),(value, valProb)) if valProb < prob => doSearch(right)
          case ((value, valProb),(_,_)) if valProb > prob => doSearch(left)
          case ((_, _),(value,_)) => value
        }
      }
    }
    doSearch(data)
  }


  /**
    * Loads the distribution from the provided file
    * @param parameters The first element must be the path to the file, The second element must be the separator
    */
  override def initialize(parameters: Any*): Unit = {
    val fileName = parameters(0) match { case path : String => path }
    val separator = parameters(1) match { case separator : String => separator }

    val inputFileLines : List[String] = Source.fromFile(fileName).
                                            getLines().toList

    val values : List[(T,Double)]  = inputFileLines.map( line => line.split(separator))
                                                   .map( { case Array(value,prob) => (parser(value),prob.toDouble)})

    val probabilitiesSum = values.foldLeft(0.0)( { case (acc, (_,prob)) => acc + prob} )

    if( (1.0-probabilitiesSum) > 0.001 || probabilitiesSum > 1.0) {
      throw new RuntimeException(s"Invalid input file. Probabilities do not add 1 but $probabilitiesSum")
    }

    data = values.drop(1)
                 .scanLeft(values(0))({ case ((prevValue,accProb),(value,prob)) => (value,accProb+prob)})
                 .toArray

  }

  override def run(id: Long, random: Long, dependencies: Any*) : T = {
    val prob = RndGenerator.toDouble(random)
    binarySearchForValue(data,prob)
  }
}
