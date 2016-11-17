/**
  * Created by aprat on 17/10/16.
  */
package org.dama.datasynth.tests

import no.uib.cipr.matrix.{DenseMatrix, DenseVector, Matrix, Vector}
import org.dama.datasynth.utils.{Algorithms, MatrixBuilder}

import scala.io.Source
import scala.util.Random

object HomophilyIsomorphism {

  var countryPosition : scala.collection.mutable.HashMap[String,(Int,Int)] = new scala.collection.mutable.HashMap[String,(Int,Int)]()
  var weightCountry = 0.5
  var weightUniversity = 0.35
  var weightAge = 0.15

  @SerialVersionUID(116L)
  class VertexAttributes (country : String, university : String, age : Int) extends Serializable {
    var country_ : String = country
    var university_ : String = university
    var age_ : Int = age
    def this() {
      this("","",0)
    }

    override def toString: String = country_ +" "+university_ + " "+age_

    def distance(other : VertexAttributes): Double = {
      weightCountry*countryDistance(country_,other.country_) +
      weightUniversity*universityDistance(university_,other.university_) +
      weightAge*ageDistance(age_,other.age_)
    }
  }

  def distance(vector1 : Seq[Any], vector2 : Seq[Any]): Double = {
    1.0 - (weightCountry*countryDistance(vector1(0).toString,vector2(0).toString) +
          weightUniversity*universityDistance(vector1(1).toString,vector2(1).toString) +
          weightAge*ageDistance(vector1(2) match { case v : Int => v},vector2(2) match { case v : Int => v}))
  }

  @SerialVersionUID(100L)
  class VertexData ( degree : Int, toReplace : Boolean, vertexAttributes : VertexAttributes ) extends Serializable{
    var degree_ = degree
    var toReplace_ : Boolean = toReplace
    var vertexAttributes_ : VertexAttributes = vertexAttributes

    def this()  {
      this(0,false, new VertexAttributes())
    }

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
    val edges = Source.fromFile ("/home/aprat/projects/oracle/datageneration/network.csv").getLines().map (line => line.split ("\t") ).map (line => (line (0).toInt-1, line (1).toInt-1)).toList

    var random : Random = new Random

    println("Building structural graph adjacency matrix")
    val structuralGraph : Matrix = MatrixBuilder.buildMatrixFromEdges(1000,edges)
    println("Building vector graph adjacency matrix")
    val vectorGraph : Matrix = MatrixBuilder.buildMatrixFromVectors(attributes,distance)
    val dampings = Range(0,100).map( _ => random.nextDouble()).toArray[Double]

    println("Building structural graph signature matrix")
    val structuralGraphSignature : Matrix = MatrixBuilder.buildSignatures(structuralGraph,dampings,10)
    println("Building structural graph signature matrix")
    val vectorGraphSignature : Matrix = MatrixBuilder.buildSignatures(vectorGraph,dampings,10)

    class EulerDistance( a : Matrix, b : Matrix ) {
      val a_ : Matrix = a
      val b_ : Matrix = b
      val costMatrix_ : DenseMatrix =  new DenseMatrix(a.numColumns,b.numRows)
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
    val matching : Array[Int] = Algorithms.findMatching(structuralGraphSignature.numRows(), vectorGraphSignature.numRows, euler.distance)
    var outputFile = new java.io.File("/home/aprat/projects/oracle/datageneration/output.csv")
    var writer = new java.io.PrintWriter(outputFile)
    writer.println("Id Country University Age")
    Range(0,matching.length).foreach(i => {
      val attrs = attributes(matching(i))
      writer.println((i+1)+" "+attrs(0)+" "+attrs(1)+" "+attrs(2))
    })
    writer.close()
  }
}
