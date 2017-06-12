package org.dama.datasynth.common.generators.property.dummy

import org.dama.datasynth.common.generators.property.PropertyGenerator

/**
  * Created by aprat on 11/04/17.
  *
  *
  *
  * Dummy property generator that produces a Boolean
  *
  */

class DummyBooleanPropertyGenerator( value : Boolean )
  extends PropertyGenerator[Boolean] {

  override def run(id: Long, random: Long, dependencies: Any*) : Boolean = value
}
