package org.dama.datasynth.runtime.generators

import org.dama.datasynth.common.PropertyGenerator

/**
  * Created by aprat on 11/04/17.
  *
  *
  * Dummy property generator that produces a Double
  *
  */

class dummyDoublePropertyGenerator
  extends PropertyGenerator[Double] {

  var num : Double = 0.0

  override def initialize(parameters: Any*): Unit  = {
    num = parameters.apply(0) match {case x : Double => x}
  }

  override def run(id: Long, random: Long, dependencies: Any*) : Double = num
}
