package org.dama.datasynth.common.generators.property.dummy

import org.dama.datasynth.common.generators.property.PropertyGenerator

/**
  * Created by aprat on 11/04/17.
  *
  * Dummy property generator that produces a Long
  */

class DummyLongMultPropertyGenerator
  extends PropertyGenerator[Long] {


  override def initialize(parameters: Any*): Unit  = {
  }

  override def run(id: Long, random: Long, dependencies: Any*) : Long = {
    val mult1 = dependencies(0) match { case x : Long => x}
    val mult2 = dependencies(1) match { case x : Long => x}
    mult1 * mult2
  }
}
