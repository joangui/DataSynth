package org.dama.datasynth.runtime.generators

import org.dama.datasynth.common.PropertyGenerator

/**
  * Created by aprat on 11/04/17.
  *
  *
  * Dummy property generator that produces an Int
  *
  */

class dummyIntPropertyGenerator
  extends PropertyGenerator[Int] {

  var num : Int = 0

  override def initialize(parameters: Any*): Unit  = {
    num = parameters.apply(0) match {case x : Int => x}
  }

  override def run(id: Long, random: Long, dependencies: Any*) : Int = num
}
