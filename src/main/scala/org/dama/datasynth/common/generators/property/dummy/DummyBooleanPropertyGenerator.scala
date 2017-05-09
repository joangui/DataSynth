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

class DummyBooleanPropertyGenerator
  extends PropertyGenerator[Boolean] {

  var value : Boolean = false

  override def initialize(parameters: Any*): Unit  = {
    value = parameters.apply(0) match {case x : Boolean => x}
  }

  override def run(id: Long, random: Long, dependencies: Any*) : Boolean = value
}
