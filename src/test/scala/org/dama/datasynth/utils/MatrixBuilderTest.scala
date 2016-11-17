package org.dama.datasynth.utils

import no.uib.cipr.matrix.{DenseMatrix, EVD, Matrices, Matrix}
import org.scalatest.junit.AssertionsForJUnit
import org.junit.Assert._
import org.junit.Test

/**
  * Created by aprat on 15/11/16.
  */
class MatrixBuilderTest extends AssertionsForJUnit {

  @Test def testBuildMatrixFromEdges: Unit = {

    val edges : List[(Int,Int)] = List((0,1),(0,2), (0,3), (1,2), (1,3),(2,3))
    val adjacencyMatrix : Matrix = MatrixBuilder.buildMatrixFromEdges(4,edges)
    Range(0,4).foreach( i => Range(0,4).foreach( j => {
      if( i != j) {
        val cellValue: Double = adjacencyMatrix.get(i, j)
        assertTrue(cellValue == 1.0)
      }
    }
    ))
  }

  @Test def testBuildMatrixFromVectors: Unit = {

    def f(vector1 : Seq[Any], vector2 : Seq[Any]): Double = {
      1.0
    }
    val vectors : List[Seq[Int]] = List(Seq(0),Seq(0), Seq(0), Seq(0))
    val adjacencyMatrix : Matrix = MatrixBuilder.buildMatrixFromVectors(vectors,f)

    Range(0,4).foreach( i => Range(0,4).foreach( j => {
      if( i != j) {
        val cellValue: Double = adjacencyMatrix.get(i, j)
        assertTrue(cellValue == 1.0)
      }
    }
    ))
  }

  @Test def testBuildSignaturesNonWeighted: Unit = {


    var matrix : DenseMatrix = new DenseMatrix(2,2)

    val edges : List[(Int,Int)] = List((0,1),(0,2),(0,3), (1,2), (1,3),(2,3))
    val adjacencyMatrix : Matrix = MatrixBuilder.buildMatrixFromEdges(4,edges)
    val dampings : Array[Double] = Array(0.1, 0.25, 0.50, 0.75)
    val signatureMatrix : Matrix = MatrixBuilder.buildSignatures(adjacencyMatrix,dampings,100)
    assertTrue(signatureMatrix.numColumns() == 4)
    assertTrue(signatureMatrix.numRows() == 4)

    Range(0,4).foreach( i => Range(0,4).foreach( j => {
        val cellValue: Double = signatureMatrix.get(i, j)
        assertTrue(Math.abs(cellValue - 0.25) / 0.25 < 0.0001 )
    }
    ))
  }

}
