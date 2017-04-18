package org.dama.datasynth.runtime.generators

import org.dama.datasynth.common.PropertyGenerator

/**
  * Created by aprat on 11/04/17.
  *
  *
  * Dummy property generator that produces a Float
  *
  */

class dummyFloatPropertyGenerator
  extends PropertyGenerator[Float] {

  var num : Float = 0.0f

  override def initialize(parameters: Any*): Unit  = {
    num = parameters.apply(0) match {case x : Float => x}
  }

  override def run(id: Long, random: Long, dependencies: Any*) : Float = num
}
