package org.dama.datasynth.runtime.generators

import org.dama.datasynth.common.PropertyGenerator

/**
  * Created by aprat on 11/04/17.
  *
  * Dummy property generator that produces a Long
  */

class dummyLongPropertyGenerator
  extends PropertyGenerator[Long] {

  var num : Long = 0L

  override def initialize(parameters: Any*): Unit  = {
    num = parameters.apply(0) match {case x : Long => x}
  }

  override def run(id: Long, random: Long, dependencies: Any*) : Long = num
}
