package org.dama.datasynth.utils

import no.uib.cipr.matrix.DenseMatrix
import org.dama.datasynth.homophily.HomophilyIsomorphism
import org.junit.Assert._
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit

import scala.util.Random

/**
  * Created by aprat on 15/11/16.
  */
class HomophilyTest extends AssertionsForJUnit {

  @Test def simpleTest2: Unit = {
    val edges : List[(Int,Int)] = List(
      (0,1),
      (0,2),
      (1,2),
      (3,4),
      (3,5),
      (4,5))

    val attributes : List[Seq[Any]] = List(
      Seq("China","A",45),
      Seq("Spain","B",18),
      Seq("China","A",45),
      Seq("Spain","B",18),
      Seq("China","A",45),
      Seq("Spain","B",18)
    )

    HomophilyIsomorphism.readCountryCoordinates()
    val permutation = Homophily.matchStructureAndVectors2(edges,attributes,HomophilyIsomorphism.weight)
    println(permutation)
    val matching = Homophily.matchingFromPermutation(permutation)
    Range(0,matching.length).foreach( i => println("Node "+i+" is now node "+matching(i)+" and has attributes "+attributes(matching(i))))
  }

  @Test def simpleTest: Unit = {
    val edges : List[(Int,Int)] = List(
                                        (0,1),
                                        (0,2),
                                        (0,3),
                                        (0,4),
                                        (1,2),
                                        (1,3),
                                        (1,4),
                                        (2,3),
                                        (2,4),
                                        (3,4),
                                        (4,5),
                                        (5,6),
                                        (5,7),
                                        (5,8),
                                        (5,9),
                                        (6,7),
                                        (6,8),
                                        (6,9),
                                        (7,8),
                                        (7,9),
                                        (8,9))

    val attributes : List[Seq[Any]] = List(
      Seq("China","A",25),
      Seq("Spain","B",18),
      Seq("China","A",25),
      Seq("Spain","B",18),
      Seq("China","A",25),
      Seq("Spain","B",18),
      Seq("China","A",25),
      Seq("Spain","B",18),
      Seq("China","A",25),
      Seq("Spain","B",18)
    )

    var random : Random = new Random
    var dampings = Range(0,10).map( _ => random.nextDouble()).toArray[Double]

    HomophilyIsomorphism.readCountryCoordinates()
    //val matching = Homophily.matchStructureAndVectors(edges,attributes,HomophilyIsomorphism.weight,dampings)
    val permutation = Homophily.matchStructureAndVectors2(edges,attributes,HomophilyIsomorphism.weight)
    val matching = Homophily.matchingFromPermutation(permutation)
    Range(0,matching.length).foreach( i => println("Node "+i+" is now node "+matching(i)+" and has attributes "+attributes(matching(i))))
  }

  @Test def paperTest: Unit ={

    var matrixA = new DenseMatrix(4,4)
    matrixA.set(0,0,0.0)
    matrixA.set(0,1,5.0)
    matrixA.set(0,2,8.0)
    matrixA.set(0,3,6.0)

    matrixA.set(1,0,5.0)
    matrixA.set(1,1,0.0)
    matrixA.set(1,2,5.0)
    matrixA.set(1,3,1.0)

    matrixA.set(2,0,8.0)
    matrixA.set(2,1,5.0)
    matrixA.set(2,2,0.0)
    matrixA.set(2,3,2.0)

    matrixA.set(3,0,6.0)
    matrixA.set(3,1,1.0)
    matrixA.set(3,2,2.0)
    matrixA.set(3,3,0.0)

    var matrixB = new DenseMatrix(4,4)

    matrixB.set(0,0,0.0)
    matrixB.set(0,1,1.0)
    matrixB.set(0,2,8.0)
    matrixB.set(0,3,4.0)

    matrixB.set(1,0,1.0)
    matrixB.set(1,1,0.0)
    matrixB.set(1,2,5.0)
    matrixB.set(1,3,2.0)

    matrixB.set(2,0,8.0)
    matrixB.set(2,1,5.0)
    matrixB.set(2,2,0.0)
    matrixB.set(2,3,5.0)

    matrixB.set(3,0,4.0)
    matrixB.set(3,1,2.0)
    matrixB.set(3,2,5.0)
    matrixB.set(3,3,0.0)

    var matchings = Homophily.matchGraphs(matrixA,matrixB)
    assertTrue(matchings.get(0,2)==1.0)
    assertTrue(matchings.get(1,3)==1.0)
    assertTrue(matchings.get(2,0)==1.0)
    assertTrue(matchings.get(3,1)==1.0)
  }

  @Test def paperTest2: Unit ={
    var matrixA = new DenseMatrix(6,6)

    matrixA.set(0,0,0.0)
    matrixA.set(0,1,1.0)
    matrixA.set(0,2,1.0)
    matrixA.set(0,3,0.0)
    matrixA.set(0,4,0.0)
    matrixA.set(0,5,0.0)

    matrixA.set(1,0,1.0)
    matrixA.set(1,1,0.0)
    matrixA.set(1,2,1.0)
    matrixA.set(1,3,0.0)
    matrixA.set(1,4,0.0)
    matrixA.set(1,5,0.0)

    matrixA.set(2,0,1.0)
    matrixA.set(2,1,1.0)
    matrixA.set(2,2,0.0)
    matrixA.set(2,3,0.0)
    matrixA.set(2,4,0.0)
    matrixA.set(2,5,0.0)

    matrixA.set(3,0,0.0)
    matrixA.set(3,1,0.0)
    matrixA.set(3,2,0.0)
    matrixA.set(3,3,0.0)
    matrixA.set(3,4,1.0)
    matrixA.set(3,5,1.0)

    matrixA.set(4,0,0.0)
    matrixA.set(4,1,0.0)
    matrixA.set(4,2,0.0)
    matrixA.set(4,3,1.0)
    matrixA.set(4,4,0.0)
    matrixA.set(4,5,1.0)

    matrixA.set(5,0,0.0)
    matrixA.set(5,1,0.0)
    matrixA.set(5,2,0.0)
    matrixA.set(5,3,1.0)
    matrixA.set(5,4,1.0)
    matrixA.set(5,5,0.0)


    var matrixB = new DenseMatrix(6,6)

    matrixB.set(0,0,0.0)
    matrixB.set(0,1,0.0)
    matrixB.set(0,2,1.0)
    matrixB.set(0,3,0.0)
    matrixB.set(0,4,1.0)
    matrixB.set(0,5,0.0)

    matrixB.set(1,0,0.0)
    matrixB.set(1,1,0.0)
    matrixB.set(1,2,0.0)
    matrixB.set(1,3,1.0)
    matrixB.set(1,4,0.0)
    matrixB.set(1,5,1.0)

    matrixB.set(2,0,1.0)
    matrixB.set(2,1,0.0)
    matrixB.set(2,2,0.0)
    matrixB.set(2,3,0.0)
    matrixB.set(2,4,1.0)
    matrixB.set(2,5,0.0)

    matrixB.set(3,0,0.0)
    matrixB.set(3,1,1.0)
    matrixB.set(3,2,0.0)
    matrixB.set(3,3,0.0)
    matrixB.set(3,4,0.0)
    matrixB.set(3,5,1.0)

    matrixB.set(4,0,1.0)
    matrixB.set(4,1,0.0)
    matrixB.set(4,2,1.0)
    matrixB.set(4,3,0.0)
    matrixB.set(4,4,0.0)
    matrixB.set(4,5,0.0)

    matrixB.set(5,0,0.0)
    matrixB.set(5,1,1.0)
    matrixB.set(5,2,0.0)
    matrixB.set(5,3,1.0)
    matrixB.set(5,4,0.0)
    matrixB.set(5,5,0.0)

    var matchings = Homophily.matchGraphs(MatrixBuilder.perturbSymmetricMatrix(matrixA),MatrixBuilder.perturbSymmetricMatrix(matrixB))
  }

}
