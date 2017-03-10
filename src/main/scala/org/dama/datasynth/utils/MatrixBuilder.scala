package org.dama.datasynth.utils

import no.uib.cipr.matrix._

import scala.util.Random

/**
  * Created by aprat on 14/11/16.
  */
object MatrixBuilder {

  /**
    * Builds an adjacency matrix out of a list of edges
    * @param n The size of the graph
    * @param edges The list of edges
    * @return The build adjacency matrix
    */
  def buildMatrixFromEdges( n : Int, edges : List[(Int,Int)]): Matrix = {
    var matrix : DenseMatrix = new DenseMatrix(n,n)
    matrix.zero()
    edges.foreach(e => {
      matrix.set(e._1, e._2, 1.0)
      matrix.set(e._2, e._1, 1.0)
    })
    matrix
  }

  /**
    * Builds a similarity matrix out of a set of vectors using a provided functions
    * @param vectors The set of vectors to build the similarity matrix from
    * @param f The function used to compute the similarity
    * @return The similarity matrix where each position is the similarity between the coresponding i and j vectors
    */
  def buildMatrixFromVectors(vectors : List[Seq[Any]], f : (Seq[Any],Seq[Any]) => Double ): Matrix = {
    var matrix : DenseMatrix = new DenseMatrix(vectors.size, vectors.size)
    var i = 0
    vectors.foreach( v1 => {
      var j = 0
      vectors.foreach( v2 => {
        if( i != j) {
          val dis : Double = f(v1,v2)
          matrix.set(i,j,dis)
          matrix.set(j,i,dis)
        }
        j+=1
      }
      )
      i+=1
    })
    matrix
  }

  /**
    * Builds an adjacency matrix out of a list of edges
    * @param matrix The matrix to perturb
    * @return The build adjacency matrix
    */
  def perturbSymmetricMatrix( matrix : Matrix ): DenseMatrix = {
    var random = new Random()
    val n = matrix.numRows()
    val ret : DenseMatrix = new DenseMatrix(n,n)
    Range(0,n).foreach( i => {
      Range(0,n).foreach( j => {
        val value = matrix.get(i,j)
        if(i<j) {
        ret.set(i,j,{
          val prob = random.nextDouble
          prob < value match {
            case true => Math.min(1.0,1.0 + 2*(random.nextDouble()-0.5) / n)
            case false => Math.max(0.0,0.0 + 2*(random.nextDouble()-0.5) / n)
          }
        })
          ret.set(j,i,ret.get(i,j))
        }
      })
    })
    ret
  }

  def normalizeCostMatrixValues( matrix : Matrix ) : Matrix = {
    val n = matrix.numRows()
    var retMatrix : DenseMatrix = new DenseMatrix(n,n)
    retMatrix.zero
    var iter = matrix.iterator()
    var min = 1.0
    var max = 0.0
    while(iter.hasNext) {
      val next = iter.next
      if(next.get() != 0.0) {
        min = next.get() < min match {
          case true => next.get()
          case false => min
        }

        max = next.get() > max match {
          case true => next.get()
          case false => max
        }
      }
    }

    iter = matrix.iterator
    while(iter.hasNext) {
      var next = iter.next
      if(next.get != 0.0) {
        retMatrix.set(next.row, next.column, (next.get - min) / (max - min))
      }
    }
    retMatrix
  }

  def findStrictDecreasingEigenValuePermutation( eigenValues : Array[Double]) : Matrix = {
    val n : Int = eigenValues.length
    var retMatrix : DenseMatrix = new DenseMatrix(n,n)
    retMatrix.zero()
    var tuples = Range(0,n).map( i => {
      (i,eigenValues(i))}).sortWith( (a,b) => a._2 > b._2)
    Range(0,n).foreach( i => {
      retMatrix.set(i,tuples(i)._1,1.0)
    }
    )
    retMatrix
  }

  def buildSumPowersOfDiagonalMatrix( matrix : Matrix, factor : Double): Matrix = {
    val n = matrix.numRows()
    var retMatrix : DenseMatrix = new DenseMatrix(n,n)
    Range(0,n).foreach(
      i => {
        val x: Double = factor * matrix.get(i, i) match {
          case 1.0 => 0.0
          case x => (1.0 / (1.0 - x))
        }
        retMatrix.set(i, i, x)
      }
    )
    retMatrix
  }

  /**
    * Computes the signature of a given adjacency matrix
    * @param matrix The adjacency matrix to compute the signature from
    * @return A matrix containing the signature of each vertex
    */
  def buildSignatures( matrix : Matrix, dampings : Array[Double]) : Matrix = {

    println("building matrix W")
    var s = System.nanoTime();
    val n : Int = matrix.numRows;
    val d = dampings.sortWith( _ < _ )

    var diagonal : Array[Double] = Array.fill(n)(0)
    var iterator = matrix.iterator()
    while(iterator.hasNext) {
      val entry = iterator.next();
      diagonal(entry.row())+=entry.get()
    }
    var theta : DenseMatrix = new DenseMatrix(n, n)
    theta.zero()
    for( i <- Range(0,diagonal.length)) {
      if(diagonal(i) > 0.0)
        theta.set(i,i,1.0/diagonal(i))
    }
    var w : DenseMatrix = new DenseMatrix(n,n);
    theta.mult(matrix,w)
    var wT : DenseMatrix = new DenseMatrix(n,n)
    w.transpose(wT)
    println("Matrix W built in "+ (System.nanoTime() - s)/1000000+ "ms")

    val I = Matrices.identity(n)
    val wEVD : EVD = new EVD(n,false,true)
    wEVD.factor(wT)
    val rightEigenVectors = wEVD.getRightEigenvectors
    val rightEigenVectorsInverse = I.copy()
    rightEigenVectors.solve(I,rightEigenVectorsInverse)
    val eigenValues = wEVD.getRealEigenvalues()
    val eigenMatrix : DenseMatrix = new DenseMatrix(n,n)
    Range(0,n).foreach(
      i => {
        eigenMatrix.set(i,i,eigenValues(i))
      }
    )

    println("Computing signatures")
    s = System.nanoTime();
    var columnCounter : Int = 0
    var signature : DenseMatrix  = new DenseMatrix(n,n)
    d.foreach( damping => {
      println("Computing for damping value "+columnCounter+" "+damping)
      val start = System.nanoTime()
      var t = System.nanoTime()
      val sumPowers = buildSumPowersOfDiagonalMatrix(eigenMatrix,damping)
      println("Time to build sum of powers of diagonal matrix: "+(System.nanoTime() - t)/1000000+ "ms")
      val temp : DenseMatrix = new DenseMatrix(n,n)
      //rightEigenVectors.mult(sumPowers,temp)

      //Multiply this way because sumPowers is a diagonal matrix
      for( i <- Range(0,n)) {
        for( j <- Range(0,n)) {
          temp.set(i,j,rightEigenVectors.get(i,j)*sumPowers.get(j,j))
        }
      }

      val temp2 : DenseMatrix = new DenseMatrix(n,n)
      temp.mult(rightEigenVectorsInverse,temp2)
      t = System.nanoTime()
      var tempSignature = new DenseVector(n)
      val factor = ((1.0-damping)/n)
      var sumOfsums = 0.0
      for( i <- Range(0,n)) {
        var sum : Double = 0.0
        for( j <- Range(0,n)) {
          sum += temp2.get(i,j)
        }
        tempSignature.set(i,factor*sum)
        sumOfsums += factor*sum
      }

      for( i <- Range(0,n)) {
        signature.set( i, columnCounter, tempSignature.get(i)/sumOfsums )
      }

      println("Cost of signature population "+ (System.nanoTime() - t)/1000000 + " ms")
      columnCounter+=1
      println("Time of iteration: "+(System.nanoTime() - start)/1000000+ "ms")
    })
    println("Signatures built in "+ (System.nanoTime() - s)/1000000+ "ms")
    signature
  }

}
