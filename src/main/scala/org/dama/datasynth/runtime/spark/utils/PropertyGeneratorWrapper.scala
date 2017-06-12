package org.dama.datasynth.runtime.spark.utils

import org.dama.datasynth.common.generators.property.PropertyGenerator

/**
  * Created by aprat on 19/04/17.
  *
  * This class wraps a PropertyGenerator and a random number generator. The class provides
  * a run method to generate a property given a node id.
  */
class PropertyGeneratorWrapper[T]( pg : PropertyGenerator[T],
                                        rndGen : RndGenerator,
                                        dependentPGs : Seq[PropertyGeneratorWrapper[_]]) extends Serializable{
  def run( id: Long ): T = {
    val params : Seq[Any] = dependentPGs.map( pg => pg.run(id))
    pg.run(id,rndGen.random(id),params : _*)
  }

  def apply(id : Long) : T = run(id)

}
