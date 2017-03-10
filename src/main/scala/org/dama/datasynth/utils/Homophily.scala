package org.dama.datasynth.utils

import no.uib.cipr.matrix.{DenseMatrix, EVD, Matrix}
import org.dama.datasynth.algorithms.HungarianAlgorithm

import scala.util.Random

/**
  * Created by aprat on 18/11/16.
  */
object Homophily {

  def matchStructureAndVectors( edges : List[(Int,Int)], attributes : List[Seq[Any]], weight : (Seq[Any], Seq[Any]) => Double, dampings : Array[Double]): Array[Int] = {

    println("Building structural graph adjacency matrix")
    val structuralGraph : Matrix = MatrixBuilder.perturbSymmetricMatrix(MatrixBuilder.buildMatrixFromEdges(attributes.length,edges))
    println("Building vector graph adjacency matrix")
    val vectorGraph : Matrix = MatrixBuilder.perturbSymmetricMatrix(MatrixBuilder.buildMatrixFromVectors(attributes,weight))


    println("Building structural graph signature matrix")
    val structuralGraphSignature : Matrix = MatrixBuilder.buildSignatures(structuralGraph,dampings)
    println("Building structural graph signature matrix")
    val vectorGraphSignature : Matrix = MatrixBuilder.buildSignatures(vectorGraph,dampings)

    //println(structuralGraphSignature)
    //println(vectorGraphSignature)

    class EulerDistance( a : Matrix, b : Matrix ) {
      val a_ : Matrix = a
      val b_ : Matrix = b
      val costMatrix_ : DenseMatrix =  new DenseMatrix(a.numColumns,b.numRows)
      val random = new Random
      a_.add(-1.0,b_)
      Range(0,a_.numRows).foreach( i =>
        Range(0,a_.numRows).foreach( j =>
          costMatrix_.set(i,j,a.get(i,j)*a.get(i,j))
        )
      )
      def distance( x : Int, y : Int) : Double = {
        costMatrix_.get(x,y)
      }
    }

    val euler = new EulerDistance(structuralGraphSignature, vectorGraphSignature)
    println("Running Hungarian Algorithm to find the matching")
    Algorithms.findMatching(structuralGraphSignature.numRows, vectorGraphSignature.numRows, euler.distance)
  }

  /**
    * Retuns a matching between two graphs. The matching is a function (array position) that maps rows of B to rows of A
    * @param A Matrix A to match
    * @param B Matrix B to match
    * @return The matching.
    */
  def matchGraphs( A : DenseMatrix , B : DenseMatrix ) : DenseMatrix = {

    var n = A.numColumns()

    var AEVD = new EVD(n,false,true)
    AEVD.factor(A)
    var AEigenValues = AEVD.getRealEigenvalues
    var ARightEV = new DenseMatrix(AEVD.getRightEigenvectors)
    var AStrictDecreasingPermutation = MatrixBuilder.findStrictDecreasingEigenValuePermutation(AEigenValues)
    var AStrictDecreasingPermutationT = new DenseMatrix(n,n)
    AStrictDecreasingPermutation.transpose(AStrictDecreasingPermutationT)
    var AU = new DenseMatrix(n,n)
    ARightEV.mult(AStrictDecreasingPermutationT,AU)
    var AUT = new DenseMatrix(n,n)
    AU.transpose(AUT)

    var BEVD = new EVD(n,false,true)
    BEVD.factor(B)
    var BEigenValues = BEVD.getRealEigenvalues
    var BRightEV = new DenseMatrix(BEVD.getRightEigenvectors)
    var BStrictDecreasingPermutation = MatrixBuilder.findStrictDecreasingEigenValuePermutation(BEigenValues)
    var BStrictDecreasingPermutationT = new DenseMatrix(n,n)
    BStrictDecreasingPermutation.transpose(BStrictDecreasingPermutationT)
    var BU = new DenseMatrix(n,n)
    BRightEV.mult(BStrictDecreasingPermutationT,BU)
    var BUT = new DenseMatrix(n,n)
    BU.transpose(BUT)


    Range(0,n).foreach( i => Range(0,n).foreach(
      j => {
        AUT.set(i,j,Math.abs(AUT.get(i,j)))
        BU.set(i,j,Math.abs(BU.get(i,j)))
      }
    ))


    var mult = new DenseMatrix(n,n)
    BU.mult(AUT,mult)

    val costMatrix = Array.ofDim[Double](n,n)
    Range(0,n).foreach( i =>
      Range(0,n).foreach( j =>
        costMatrix(i)(j) = 1-mult.get(i,j)
      )
    )

    var hungarianAlgorithm = new HungarianAlgorithm(costMatrix)
    var matching = hungarianAlgorithm.execute()
    var permutation = new DenseMatrix(n,n)
    permutation.zero()
    matching.indices.foreach(i => permutation.set(matching(i),i,1.0))

    /*println("PT")
    println(permutation)
    */

    new DenseMatrix(permutation.transpose())
  }

  def reverseMatching( matching : Array[Int]) : Array[Int] = {
    var retMatching : Array[Int] = Array.ofDim[Int](matching.length)
    matching.indices.foreach( i => retMatching(matching(i)) = i)
    retMatching
  }

  def matchingFromPermutation( permutation : DenseMatrix ) : Array[Int] = {
    var matching = Array.ofDim[Int](permutation.numRows())
    var iterator = permutation.iterator()
    while(iterator.hasNext) {
      val next = iterator.next
      if(next.get() == 1.0) {
        matching(next.column) = next.row
      }
    }
    matching
  }

  def computeHomophily(edges : List[(Int,Int)], attributes : List[Seq[Any]], weight : (Seq[Any], Seq[Any]) => Double, matching : Array[Int] ) : Double = {
    var sum = 0.0
    edges.foreach( e => sum += weight(attributes(matching(e._1)), attributes(matching(e._2))))
    return sum / edges.length
  }

  def matchStructureAndVectors2( edges : List[(Int,Int)], attributes : List[Seq[Any]], distance : (Seq[Any], Seq[Any]) => Double): DenseMatrix = {

    println("Building structural graph adjacency matrix")
    var structuralGraph = new DenseMatrix(MatrixBuilder.perturbSymmetricMatrix(MatrixBuilder.buildMatrixFromEdges(attributes.length,edges)))
    println("Building vector graph adjacency matrix")
    var vectorGraph = new DenseMatrix(MatrixBuilder.perturbSymmetricMatrix(MatrixBuilder.buildMatrixFromVectors(attributes,distance)))

    var outputFile = new java.io.File("/home/aprat/projects/oracle/datageneration/problem.dat")
    var writer = new java.io.PrintWriter(outputFile)
    val n = structuralGraph.numColumns()
    writer.println(n)
    writer.println(structuralGraph)
    writer.println(vectorGraph)
    writer.close()

    matchGraphs(structuralGraph,vectorGraph)
  }
}
