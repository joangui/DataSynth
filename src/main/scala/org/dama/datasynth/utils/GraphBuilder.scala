package org.dama.datasynth.utils

/**
  * Created by aprat on 14/11/16.
  */
object GraphBuilder {

  /**
    * Builds an adjacency matrix out of a list of edges
    * @param n The size of the graph
    * @param edges The list of edges
    * @return The build adjacency matrix
    */
  def buildGraphFromEdges( n : Int, edges : List[(Int,Int)]): Graph = {
    var graph : Graph = new Graph(n)
    edges.foreach(e => {
      graph.addNeighbor(e._1,e._2,1.0)
      graph.addNeighbor(e._2,e._1,1.0)
    })
    graph
  }

  /**
    * Builds a similarity matrix out of a set of vectors using a provided functions
    * @param vectors The set of vectors to build the similarity matrix from
    * @param f The function used to compute the similarity
    * @return The similarity matrix where each position is the similarity between the coresponding i and j vectors
    */
  def buildGraphFromVectors[T](vectors : List[T], f : (T,T) => Double ): Graph  = {
    var graph : Graph = new Graph(vectors.length)
    var i = 0
    vectors.foreach( v1 => {
      var j = 0
      vectors.foreach( v2 => {
        val dis : Double = f(v1,v2)
        graph.addNeighbor(i,j,dis)
        j+=1
      }
      )
      i+=1
    })
    graph
  }

  /**
    * Returns a normalized version of the graph, where the weights have been normalized between a minimum and a maximum
    * @param graph The graph to normalize
    * @return A normalized version of the graph
    */
  def normalizeGraphValues( graph : Graph ) : Graph = {
    val n = graph.size
    var retGraph =  new Graph(n)
    var min : Double = 1.0
    var max : Double = 0.0
    graph.nodes.foreach( i => {
      graph.neighbors(i).foreach( x => {
        min = x._2 < min match {
          case true => x._2
          case false => min
        }

        max = x._2 > max match {
          case true => x._2
          case false => max
        }

      })
    })

    graph.nodes.foreach(i => {
      graph.neighbors(i).foreach( j => {
        retGraph.addNeighbor(i,j._1,(j._2 - min) / (max - min))
      })
    })
    retGraph
  }
}
