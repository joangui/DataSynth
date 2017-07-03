package org.dama.datasynth.common.generators.property.dummy

import org.dama.datasynth.common.generators.property.PropertyGenerator

/**
  * Created by aprat on 11/04/17.
  *
  * Dummy property generator that produces a Long
  */

class DummyLongPropertyGenerator( num : Long )
  extends PropertyGenerator[Long] {
  override def run(id: Long, random: Long, dependencies: Any*) : Long = num
}
