package org.dama.datasynth.common.generators.property.dummy

import org.dama.datasynth.common.generators.property.PropertyGenerator

/**
  * Created by aprat on 11/04/17.
  *
  *
  * Dummy property generator that produces an Int
  *
  */

class DummyIntPropertyGenerator( num : Int )
  extends PropertyGenerator[Int] {

  override def run(id: Long, random: Long, dependencies: Any*) : Int = num
}
