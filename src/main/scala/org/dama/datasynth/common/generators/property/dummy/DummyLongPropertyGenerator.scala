package org.dama.datasynth.common.generators.property.dummy

import org.dama.datasynth.common.generators.property.PropertyGenerator

/**
  * Created by aprat on 11/04/17.
  *
  * Dummy property generator that produces a Long
  */

class DummyLongPropertyGenerator
  extends PropertyGenerator[Long] {

  var num : Long = 0L

  override def initialize(parameters: Any*): Unit  = {
    num = parameters.apply(0) match {case x : Long => x}
  }

  override def run(id: Long, random: Long, dependencies: Any*) : Long = num
}
