package org.dama.datasynth.common.generators.property

import scala.util.Try

/**
  * Created by aprat on 7/04/17.
  */

object PropertyGenerator {

  def getInstance[T]( name : String ) : Try[PropertyGenerator[T]] = {
    Try(Class.forName(name).newInstance().asInstanceOf[PropertyGenerator[T]])
  }
}

abstract class PropertyGenerator[T] extends Serializable {
  def initialize( parameters : Any* )
  def run( id : Long , random : Long, dependencies : Any* ) : T
}


