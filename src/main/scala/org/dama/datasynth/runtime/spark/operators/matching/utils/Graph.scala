package org.dama.datasynth.runtime.spark.operators.matching.utils

import org.apache.spark.sql.DataFrame

import scala.collection.mutable

/**
  * Created by joangui on 19/07/2017.
  */
class Graph(edgeDataFrame:DataFrame) extends mutable.HashMap[Long,mutable.Set[Long]]{
var numEdges = 0




}
