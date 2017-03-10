package org.dama.datasynth.utils

import org.junit.Assert._
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit

/**
  * Created by aprat on 15/11/16.
  */
class GraphBuilderTest extends AssertionsForJUnit {


  @Test def testBuildMatrixFromEdges: Unit = {
    val edges : List[(Int,Int)] = List((0,1),(0,2), (0,3), (1,2), (1,3),(2,3))
    val graph = GraphBuilder.buildGraphFromEdges(4,edges)
    assertTrue(graph.hasNeighbor(0,1))
    assertTrue(graph.hasNeighbor(0,2))
    assertTrue(graph.hasNeighbor(0,3))
    assertTrue(graph.hasNeighbor(1,2))
    assertTrue(graph.hasNeighbor(1,3))
    assertTrue(graph.hasNeighbor(2,3))
  }

  @Test def testBuildMatrixFromVectors: Unit = {

    def f(vector1 : Seq[Any], vector2 : Seq[Any]): Double = {
      1.0
    }
    val vectors : List[Seq[Int]] = List(Seq(0),Seq(0), Seq(0), Seq(0))
    val graph = GraphBuilder.buildGraphFromVectors(vectors,f)
    graph.nodes.foreach( i => {
      graph.neighbors(i).foreach( j => assertTrue(j._2 == 1.0))
    } )
  }

}
