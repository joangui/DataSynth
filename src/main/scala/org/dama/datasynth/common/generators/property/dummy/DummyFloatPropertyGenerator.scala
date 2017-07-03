package org.dama.datasynth.common.generators.property.dummy

import org.dama.datasynth.common.generators.property.PropertyGenerator

/**
  * Created by aprat on 11/04/17.
  *
  *
  * Dummy property generator that produces a Float
  *
  */

class DummyFloatPropertyGenerator( num : Float )
  extends PropertyGenerator[Float] {

  override def run(id: Long, random: Long, dependencies: Any*) : Float = num
}
