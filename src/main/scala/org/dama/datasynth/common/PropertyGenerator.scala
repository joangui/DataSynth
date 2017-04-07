package org.dama.datasynth.common

/**
  * Created by aprat on 7/04/17.
  */
abstract class PropertyGenerator[T] {
  def initialize( parameters : Any* )
  def run( id : Long , random : Long, dependencies : Any* ) : T
}
