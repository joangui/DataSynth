package org.dama.datasynth.runtime.generators

import org.dama.datasynth.common.PropertyGenerator

/**
  * Created by aprat on 11/04/17.
  *
  * Dummy property generator that produces a String
  */

class dummyStringPropertyGenerator
  extends PropertyGenerator[String] {

  var value : String = ""

  override def initialize(parameters: Any*): Unit  = {
    value = parameters.apply(0) match {case x : String => x}
  }

  override def run(id: Long, random: Long, dependencies: Any*) : String = value
}
