package org.dama.datasynth.utils

import scala.collection.mutable

/**
  * Created by aprat on 21/12/16.
  */
class Dictionary {
  var hashMap = new mutable.HashMap[String,Int]
  var nextIndex = 0

  def add( value : String ): Unit = {
    if(!hashMap.contains(value)) {
      hashMap.put(value, nextIndex)
      nextIndex += 1
    }
  }

  def get( value : String ) : Int = {
    hashMap.get(value).get
  }

  def apply(value : String) : Int = get(value)

}
