package org.dama.datasynth.common.generators.property.empirical

/**
  * Created by aprat on 12/05/17.
  */
class LongGenerator(fileName : String, separator : String ) extends DistributionBasedGenerator[Long](str => str.toLong, fileName, separator )
