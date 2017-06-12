package org.dama.datasynth.common.generators.property.empirical

/**
  * Created by aprat on 12/05/17.
  */
class IntGenerator( fileName : String, separator : String ) extends DistributionBasedGenerator[Int]( str => str.toInt, fileName, separator )
