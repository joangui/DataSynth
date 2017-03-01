/**
  * Created by aprat on 17/10/16.
  */
package org.dama.datasynth.homophily

import no.uib.cipr.matrix.{DenseMatrix, Matrix}
import org.dama.datasynth.utils._

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.io.Source
import scala.util.Random

object HomophilyQAP {

  var countryPosition : scala.collection.mutable.HashMap[String,(Int,Int)] = new scala.collection.mutable.HashMap[String,(Int,Int)]()
  var weightCountry = 0.6
  var weightUniversity = 0.20
  var weightAge = 0.20

  /**
    * Computes the weight between two vectors of features (Country, University, Age)
    * @param vector1 The first vector to compare
    * @param vector2 The second vector to compare
    * @return A real number between 0 and 1 indicating the weight between the two vectors. 1 means maximum similarity, 0 means no similaity
    */
  /*def weight(vector1 : Seq[Any], vector2 : Seq[Any]): Double = {
   1.0 - (weightCountry*countryDistance(vector1(0).toString,vector2(0).toString) +
      weightUniversity*universityDistance(vector1(1).toString,vector2(1).toString) +
      weightAge*ageDistance(vector1(2) match { case v : Int => v},vector2(2) match { case v : Int => v}))
  }*/

  /**
    * Computes the euclidean distance between two countries using the lattitude and the longitude as
    * euclidean coordinates, normalized between 0 and 1.
    *
    * @param country1 The first country to compute the distance from
    * @param country2 The second country to compute the distance from
    * @return Returns the distance between two countries. The distance is normalized between 0 and 1.
    *         1 means maximum distance, 0 minimum distance
    */
  def countryDistance(country1: String, country2: String): Double = {
    val position1 = countryPosition(country1)
    val position2 = countryPosition(country2)
    return Math.sqrt(Math.pow(position1._1-position2._1,2) + Math.pow(position1._2-position2._2,2)) / 509.0
  }

  /**
    * Computes the distance between two ages. The distance is the absolute difference between both ages normalized between zero and one using the minimum (15) and the maximum (55)
    * @param age1 The first age to compute the distance of
    * @param age2 The second age to compute the distance of
    * @return The distance between both ages normalized between 0 and 1.
    */
  def ageDistance( age1 : Int, age2 : Int ) : Double = {
    Math.abs(age1 - age2) / 45.0
  }

  /**
    * Computes the distance between two universities. The distance is a real values between 0 and 1
    * @param university1 The first university to compute the distance of
    * @param university2 The second university to compute the distance of
    * @return The distance between two universities, normalized between 0 and 1.
    */
  def universityDistance( university1 : String, university2 : String): Double = {
    university1.compareTo(university2) match {
      case 0 => 0.0
      case _ => 1.0
    }
  }

  /**
    * Reads the coordinates of the countries
    */
  def readCountryCoordinates() = {
    val coordinates =  Source.fromFile("/home/aprat/projects/oracle/datageneration/dicLocations.txt").getLines.map(line => line.split(" ")).map( line => (line(1),line(2), line(3)))
    coordinates.foreach( entry => countryPosition += ( entry._1 -> (Integer.parseInt(entry._2),Integer.parseInt(entry._3))))
  }

  /**
    * Computes the cost of a given solution
    * @param graphA The first graph to compute the cost of the mapping
    * @param graphB The second graph to compute the cost of the mapping
    * @param solution The solution represented as an array of integers, mapping nodes of the first graph to nodes of the second graph
    * @return The cost of the solution, The larger the better.
    */
  def cost( graphA : Graph , graphB : Graph, solution : Array[Int]) : Double = {
    var numInstances : Int = 0
    graphA.nodes.map( i => costOfNode(graphA,i,graphB,solution)).reduce( (a,b) => a + b)
  }

  /**
    * The cost incurred by a node in the given solution
    * @param graphA The first graph
    * @param node The node of the first graph to compute the cost of
    * @param graphB The second graph
    * @param solution The solution to compute the cost of the node of
    * @return The cost of the given node in the given solution
    */
  def costOfNode( graphA : Graph, node : Int, graphB : Graph, solution : Array[Int]) : Double = {
    var costValue : Double = 0.0
    graphA.neighbors(node).map( j => {
      var attributeIndexI = solution(node)
      var attributeIndexJ = solution(j._1)
      graphB.neighborValue(attributeIndexI,attributeIndexJ)
    }).reduce( (a,b) => a + b)
  }

  /**
    * Finds a list of neighboring solution, represented as a swap between two nodes in the solution, and the associated cost
    * @param graphA The first graph
    * @param graphB The second graph
    * @param solution The array representing the solutuon, mapping nodes of the first graph to the second graph
    * @return A list of triplets (int, int, double), representing a neighboring solution (swapped nodes) as well as the new cost of the solution
    */
  def neighborSolutions( graphA : Graph , graphB : Graph, solution : Array[Int]) : List[(Int,Int,Double)] = {
    val rankedNodes = graphA.nodes.
                            map( x => (x,costOfNode(graphA,x,graphB,solution))).
                            sortWith( (a,b) => a._2/graphA.neighbors(a._1).size < b._2/graphA.neighbors(b._1).size ).map(x => x._1).toArray

    val currentSolutionCost = cost(graphA, graphB, solution)

    val windowSize = 100
    (graphA.nodes.flatMap( i => {
      graphA.neighbors(i).keySet.filter( j => i < j).map( j => {
        var newSolution = solution.clone()
        val temp = newSolution(i)
        newSolution(i) = newSolution(j)
        newSolution(j) = temp
        val newCost = currentSolutionCost -
          2*costOfNode(graphA,i,graphB,solution) -
          2*costOfNode(graphA,j,graphB,solution) +
          2*costOfNode(graphA,i,graphB,newSolution) +
          2*costOfNode(graphA,j,graphB,newSolution)
        (i,j, newCost)
      })
    }).filter( x => { graphB.neighborValue(x._1,x._2) != 1.0 }).toList ++
        Range(0,windowSize).flatMap( i => {
          val nodeI = rankedNodes(i)
          Range(i+1,windowSize).filter( j => !graphA.hasNeighbor(nodeI,rankedNodes(j))).map(
            j => {
              val nodeJ = rankedNodes(j)
              var newSolution = solution.clone()
              val temp = newSolution(nodeI)
              newSolution(nodeI) = newSolution(nodeJ)
              newSolution(nodeJ) = temp
              val newCost = currentSolutionCost -
                2*costOfNode(graphA,nodeI,graphB,solution) -
                2*costOfNode(graphA,nodeJ,graphB,solution) +
                2*costOfNode(graphA,nodeI,graphB,newSolution) +
                2*costOfNode(graphA,nodeJ,graphB,newSolution)
              (nodeI,nodeJ, newCost)
            }
          )
        }).toList
    ).sortWith( (a,b) => a._3 > b._3 )
  }


  /**
    * Finds a solution to the mapping problem, by applying a bfs traversal starting from a random node of the first graph. At each bfs step, each neighor of the
    * current frontier is assigned the remaining node of the second graph with a largest weight
    * @param graphA The first graph
    * @param graphB The second graph
    * @return The found solution
    */
  def bfsSolution( graphA : Graph , graphB : Graph ) : Array[Int] = {

    var random = new Random
    val n = graphA.size
    val placed : Array[Boolean] =  Array.fill[Boolean](n){false}
    val visited : Array[Boolean] =  Array.fill[Boolean](n){false}
    var solution : Array[Int] = Array.ofDim[Int](n)

    def place( attribute : Int, node : Int ) : Unit = {
      solution(node) = attribute
      placed(attribute) = true
      visited(node) = true
    }

    visited.indices.foreach( next => {
      if(visited(next) == false) {
        val root : Int = next
        val rootAttribute : Int = graphB.neighbors(solution(next)).
              filter(x => placed(x._1) == false).
              keySet.
              toArray.
              sortWith((x, y) => graphB.neighbors(solution(next))(x) > graphB.neighbors(solution(next))(y))(0)

        place(rootAttribute, root)

        val frontier = new mutable.ListBuffer[Int]
        frontier.append(root)
        while (frontier.nonEmpty) {
          var next = frontier.remove(0)
          val toAdd : Array[Int] = graphA.neighbors(next).filter(x => visited(x._1) == false).map(x => x._1).toArray
          if (toAdd.length > 0) {
            val candidates = graphB.neighbors(solution(next)).
              filter(x => placed(x._1) == false && x._1 != solution(next)).
              keySet.
              toArray.
              sortWith((x, y) => graphB.neighbors(solution(next))(x) > graphB.neighbors(solution(next))(y))

            toAdd.indices.foreach(i => {
              place(candidates(i), toAdd(i))
              frontier.append(toAdd(i))
            })
          }
        }
      }
    })
    solution
  }

  /**
    * Class to store the Tabu search related information
    * @param n The size of the mapping problem
    * @param m The amount of forbidden moves to remember
    */
  class Tabu ( n : Int, m : Int) {
    var maxSize = m;
    var matrix : DenseMatrix = new DenseMatrix(n,n)
    matrix.zero()
    var list : mutable.ListBuffer[(Int,Int)] = new mutable.ListBuffer[(Int, Int)]

    def isPermited( x : Int, y : Int ) : Boolean = {
      matrix.get(x,y) == 0.0
    }

    def addMovement( x : Int, y : Int ) : Unit = {
      if(list.length >= maxSize){
        val oldest = list(0)
        list.remove(0)
        matrix.set(oldest._1, oldest._2, 0.0)
        matrix.set(oldest._2, oldest._1, 0.0)
      }
      matrix.set(x,y,1.0)
      matrix.set(y,x,1.0)
      list.append((x,y))
    }
  }

  /**
    * Perform a tabu search of the mapping problem, starting from the given solution
    * @param graphA The first graph
    * @param graphB The second graph
    * @param solution The solution to start from
    * @return The best solution found
    */
  def tabuSearch( graphA : Graph , graphB : Graph, solution : Array[Int], maxIterations : Int ) : Array[Int] = {
    var bestSolutionCost = cost(graphA , graphB, solution)
    var bestSolution = solution.clone()
    var currentSolution = solution.clone()
    var currentSolutionCost = bestSolutionCost

    var tabu = new Tabu(graphA.size,graphA.size)
    var countIterations = 0
    var continue = true
    var neighbors = neighborSolutions(graphA,graphB,currentSolution)
    while(continue && countIterations < maxIterations) {
      println("Starting iteration: "+countIterations+". Current cost: "+currentSolutionCost/graphA.degree+". Best cost: "+bestSolutionCost/graphA.degree)
      continue = false
      var neighborIndex = 0;
      while(!tabu.isPermited(neighbors(neighborIndex)._1,neighbors(neighborIndex)._2) && (neighborIndex < neighbors.length)) {
        neighborIndex+=1
      }

      println(neighborIndex+" "+neighbors.length)

      if(neighborIndex < neighbors.length) {
        continue = true
        val nodeI = neighbors(neighborIndex)._1
        val nodeJ = neighbors(neighborIndex)._2
        val neighborCost = neighbors(neighborIndex)._3

        if (neighborCost <= currentSolutionCost) {
          tabu.addMovement(nodeI, nodeJ)
        }

        var newSolution = currentSolution.clone()
        val temp = newSolution(nodeI)
        newSolution(nodeI) = newSolution(nodeJ)
        newSolution(nodeJ) = temp
        currentSolution = newSolution.clone()
        currentSolutionCost = neighborCost
        if (currentSolutionCost > bestSolutionCost) {
          bestSolutionCost = currentSolutionCost
          bestSolution = currentSolution.clone()
        }
        countIterations += 1
        neighbors = neighborSolutions(graphA, graphB, currentSolution)
      }
    }
    bestSolution
  }

  def main(args:Array[String]) : Unit = {

    println("Start learning distance metric")

    // Learning distance function

    case class Person( id : Int, country : String, university : String, gender : String )

    val persons = Source.fromFile("/home/aprat/projects/oracle/datageneration/persons.csv").getLines().map(line => line.split ('|') ).map (line => new Person(Integer.parseInt(line (0)),line (1),line(2), line(3))).toList
    val pairs = Source.fromFile("/home/aprat/projects/oracle/datageneration/pairs.csv").getLines().
      map(line => line.split ('|') ).
      map (line => (Integer.parseInt(line(0)), Integer.parseInt(line(1)))).toList


    println("Building dictionaries")
    val (countryDictionary, universityDictionary, genderDictionary) = (new Dictionary, new Dictionary, new Dictionary)
    persons.foreach( person => {
      countryDictionary.add(person.country)
      universityDictionary.add(person.university)
      genderDictionary.add(person.gender)
    })

    val countryPairs = pairs.map( entry => {
      (countryDictionary.get(persons(entry._1).country), countryDictionary.get(persons(entry._2).country))
    })

    val universityPairs = pairs.map( entry => {
      (universityDictionary.get(persons(entry._1).university),universityDictionary.get(persons(entry._2).university))
    })

    val genderPairs = pairs.map( entry => {
      (genderDictionary.get(persons(entry._1).gender),genderDictionary.get(persons(entry._2).gender))
    })

    println("Extracting joint probability distributions")
    val (countrySimilarity, universitySimilarity, genderSimilarity) = (new Similarity(countryPairs), new Similarity(universityPairs), new Similarity(genderPairs))

    def weightFunction( person1 : Person, person2 : Person) = {
      val first : Double = countrySimilarity(countryDictionary.get(person1.country), countryDictionary.get(person2.country))
      /*val university1 = vector1(1).toString
      val university2 = vector2(1).toString
      val second : Double = similarities(1).similarity(dictionaries(1).get(university1), dictionaries(1).get(university2))
      val gender1 = vector1(2).toString
      val gender2 = vector2(2).toString
      val third : Double = similarities(2).similarity(dictionaries(2).get(gender1), dictionaries(2).get(gender2))
      (first + second + third) / 3.0
      */
      first
    }

    val countryConditionalProb = Probability.conditionalProbability(countryPairs)

    println("Finished similarity metric")

    // Loading data
    val attributes = Source.fromFile ("/home/aprat/projects/oracle/datageneration/attributes.csv").getLines().map (line => line.split ('|') ).map (line => new Person(0,line (1),line(2), line(3))).toList
    val edges = Source.fromFile ("/home/aprat/projects/oracle/datageneration/network.csv").getLines().map (line => line.split ('\t') ).map (line => (line (0).toInt-1, line (1).toInt-1)).filter( x => x._1 < x._2).toList

    val n = attributes.length
    val structuralGraph = GraphBuilder.buildGraphFromEdges(n, edges)
    val vectorGraph = GraphBuilder.buildGraphFromVectors(attributes,weightFunction)
    var random : Random = new Random
    val randomSolution : Array[Int] = util.Random.shuffle(Range(0,n).toList).toArray[Int]
    println("Computing Initial Solution")
    val BFSSolution = bfsSolution(structuralGraph,vectorGraph)
    println("Tabu Search")
    val bestSolution = tabuSearch(structuralGraph,vectorGraph,BFSSolution, 500)

    var differentNodes = new mutable.TreeSet[Int]()
    BFSSolution.foreach( x => differentNodes.add(x))
    println("Is solution Valid? "+(differentNodes.size == n))


    var randomSolutionCost : Double = cost(structuralGraph, vectorGraph, randomSolution)/structuralGraph.degree
    println("Random Solution: "+randomSolutionCost)
    var BFSSolutionCost : Double = cost(structuralGraph, vectorGraph, BFSSolution)/structuralGraph.degree
    println("BFS Solution: "+BFSSolutionCost)
    var bestSolutionCost : Double = cost(structuralGraph,vectorGraph,bestSolution)/structuralGraph.degree
    println("Best Solution: "+bestSolutionCost)

    var outputFile = new java.io.File("/home/aprat/projects/oracle/datageneration/output.csv")
    var writer = new java.io.PrintWriter(outputFile)
    writer.println("Id Country University Gender Cost")
    Range(0,bestSolution.length).foreach(i => {
      val person = attributes(bestSolution(i))
      writer.println((i+1)+" "+person.country+" "+person.university+" "+person.gender+" "+costOfNode(structuralGraph,i,vectorGraph,bestSolution))
    })
    writer.close()

    val countryNewConditionalProb = Probability.conditionalProbability( edges.map( edge => (countryDictionary.get(attributes(bestSolution(edge._1)).country), countryDictionary.get(attributes(bestSolution(edge._2)).country))))

    println(countryConditionalProb(countryDictionary("China"))(countryDictionary.get("China")))
    println(countryNewConditionalProb(countryDictionary("China"))(countryDictionary.get("China")))
  }
}
