package org.dama.datasynth.common

import org.dama.datasynth.executionplan.ExecutionPlan

import scala.reflect.runtime.universe.{TypeTag, typeOf}

/**
  * Created by aprat on 7/04/17.
  */

object PropertyGenerator {

  def getInstance[T]( name : String ) : PropertyGenerator[T] = {
    Class.forName(name).newInstance().asInstanceOf[PropertyGenerator[T]]
  }
}

abstract class PropertyGenerator[T] extends Serializable {
  def initialize( parameters : Any* )
  def run( id : Long , random : Long, dependencies : Any* ) : T
}


