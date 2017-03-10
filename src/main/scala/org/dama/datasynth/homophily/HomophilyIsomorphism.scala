/**
  * Created by aprat on 17/10/16.
  */
package org.dama.datasynth.homophily

import org.dama.datasynth.utils.Homophily

import scala.io.Source
import scala.util.Random

object HomophilyIsomorphism {

  var countryPosition : scala.collection.mutable.HashMap[String,(Int,Int)] = new scala.collection.mutable.HashMap[String,(Int,Int)]()
  var weightCountry = 0.6
  var weightUniversity = 0.20
  var weightAge = 0.20

  def weight(vector1 : Seq[Any], vector2 : Seq[Any]): Double = {
    1.0 - (weightCountry*countryDistance(vector1(0).toString,vector2(0).toString) +
      weightUniversity*universityDistance(vector1(1).toString,vector2(1).toString) +
      weightAge*ageDistance(vector1(2) match { case v : Int => v},vector2(2) match { case v : Int => v}))
  }

  def countryDistance(country1: String, country2: String): Double = {
    val position1 = countryPosition(country1)
    val position2 = countryPosition(country2)
    return Math.sqrt(Math.pow(position1._1-position2._1,2) + Math.pow(position1._2-position2._2,2)) / 509.0
  }

  def ageDistance( age1 : Int, age2 : Int ) : Double = {
    Math.abs(age1 - age2) / 45.0
  }

  def universityDistance( university1 : String, university2 : String): Double = {
    university1.compareTo(university2) match {
      case 0 => 0.0
      case _ => 1.0
    }
  }

  def readCountryCoordinates() = {
    val coordinates =  Source.fromFile("/home/aprat/projects/oracle/datageneration/dicLocations.txt").getLines.map(line => line.split(" ")).map( line => (line(1),line(2), line(3)))
    coordinates.foreach( entry => countryPosition += ( entry._1 -> (Integer.parseInt(entry._2),Integer.parseInt(entry._3))))
  }

  def main(args:Array[String]) : Unit = {

    println("Reading coordinates")
    readCountryCoordinates()
    println("Reading input data")
    val attributes = Source.fromFile ("/home/aprat/projects/oracle/datageneration/attributes.csv").getLines().map (line => line.split (",") ).map (line => Seq[Any](line (0),line(2), Integer.parseInt(line(1)))).toList
    val edges = Source.fromFile ("/home/aprat/projects/oracle/datageneration/network.csv").getLines().map (line => line.split ("\t") ).map (line => (line (0).toInt-1, line (1).toInt-1)).filter( x => x._1 < x._2).toList
    var random : Random = new Random
    val dampings = Range(0,10).map( _ => random.nextDouble()).toArray[Double]
    //val matching = Homophily.matchStructureAndVectors(edges,attributes,weight, dampings)
    val matching = Homophily.matchingFromPermutation(Homophily.matchStructureAndVectors2(edges,attributes,weight))
    var outputFile = new java.io.File("/home/aprat/projects/oracle/datageneration/output.csv")
    var writer = new java.io.PrintWriter(outputFile)
    println(Homophily.computeHomophily(edges,attributes,weight,Range(0,matching.length).toArray[Int]))
    println(Homophily.computeHomophily(edges,attributes,weight,matching))
    writer.println("Id Country University Age")
    Range(0,matching.length).foreach(i => {
      val attrs = attributes(matching(i))
      writer.println((i+1)+" "+attrs(0)+" "+attrs(1)+" "+attrs(2))
    })
    writer.close()
  }
}
