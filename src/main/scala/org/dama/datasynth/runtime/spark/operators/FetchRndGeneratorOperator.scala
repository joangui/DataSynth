package org.dama.datasynth.runtime.spark.operators

import org.dama.datasynth.runtime.spark.SparkRuntime
import org.dama.datasynth.runtime.spark.utils.RndGenerator

import scala.collection.mutable

/**
  * Created by aprat on 18/04/17.
  *
  * Operator that fetches the Random Number Generator associated with a given property table
  */
object FetchRndGeneratorOperator {

  // Map used to store the random number generators used by each property table
  var rndGenerators = new mutable.HashMap[String, RndGenerator]

  // Next random number generator seed
  var nextRndGeneratorSeed = 0L;

  /**
    * Gets the random number generator which is associated with a given property table
    *
    * @param propertyTableName The name of the property table
    * @return The random number generator
    */
  def apply( propertyTableName : String ) : RndGenerator = {
    rndGenerators.get(propertyTableName) match {
      case Some(r) => r
      case None => {
        val rndGenerator = new RndGenerator(nextRndGeneratorSeed)
        nextRndGeneratorSeed+=1
        rndGenerators.put(propertyTableName,rndGenerator)
        rndGenerator
      }
    }
  }

}
