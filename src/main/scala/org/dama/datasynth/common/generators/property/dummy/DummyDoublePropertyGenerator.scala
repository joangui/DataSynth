package org.dama.datasynth.common.generators.property.dummy

import org.dama.datasynth.common.generators.property.PropertyGenerator

/**
  * Created by aprat on 11/04/17.
  *
  *
  * Dummy property generator that produces a Double
  *
  */

class DummyDoublePropertyGenerator( num : Double)
  extends PropertyGenerator[Double] {

  override def run(id: Long, random: Long, dependencies: Any*) : Double = num
}
