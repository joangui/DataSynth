/**
  * Created by aprat on 17/10/16.
  */
package org.dama.datasynth.homophily

import java.util.logging.{Level, LogManager, Logger}

import org.apache.spark.{Partitioner, SparkContext}
import org.apache.spark.graphx._
import org.apache.spark.graphx.lib.LabelPropagation
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object HomophilyNaive {

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

  def computeHomophily( graph : Graph[VertexData,Int] ) : Double = {
    val messages = graph.aggregateMessages[Double]( context => {
      val dis = context.srcAttr.vertexAttributes_.distance(context.dstAttr.vertexAttributes_)
      context.sendToDst(dis)
      context.sendToSrc(dis)
    }, _ + _)

    val sum : Double = messages.reduce((a,b) => (1,a._2 + b._2))._2/2
    return 1.0 - sum/graph.edges.count()
  }

/*  def oneStepBullying( graph : Graph[VertexData,Int]) : (Graph[VertexData,Int],Double) = {
    val neighborCountries = graph.aggregateMessages[(Double,Int)](
      context => {
        val dis = context.srcAttr.vertexAttributes_.distance(context.dstAttr.vertexAttributes_)
        context.sendToDst((dis,1))
        context.sendToSrc((dis,1))
      },
      (count1, count2) => (count1._1+count2._1,count1._2+count2._2)
    ).map[(VertexId,Double)]( node =>
      (node._1, node._2._1 / node._2._2))

    val stepGraph  = graph.mapVertices[(VertexData,String)]( (id,data) => (data,"")).joinVertices[(String,Int)](neighborCountries)( (id, data, country) => {
      (new VertexData(data._1.degree_, data._1.country_, data._1.data._1.country_.compareTo(country._1) != 0 ), country._1);
    })

    // Perform the attribute reassignment
    val toReplace = stepGraph.vertices.filter( x => x._2._1.toReplace_ )
    println("Number of vertices to move: "+toReplace.count)

    val verticespercountry = toReplace.sortBy(key => key._2._1.country_).zipWithIndex.map[(Long, (VertexId, String))](a => (a._2, (a._1._1, a._1._2._1.country_)))
    val verticesperrequired = toReplace.sortBy(key => key._2._2).zipWithIndex.map[(Long, (VertexId, String))](a => (a._2, (a._1._1, a._1._2._2)))
    val joined: RDD[(VertexId, String)] = verticespercountry.join(verticesperrequired).map[(VertexId, String)](row => {
      val first = row._2._1
      val second = row._2._2
      (second._1, first._2)
    })

    var finalGraph : Graph[VertexData,Int] = graph.joinVertices[String](joined)((id, data, newCountry) => {
      new VertexData(data.degree_, newCountry, false)
    }
    )

    //(finalGraph, computeHomophily(graph))
    (finalGraph, 0.0)
  }


  def twoStepBullying( graph : Graph[VertexData,Int] ): (Graph[VertexData,Int],Double) = {

    @SerialVersionUID(114L)
    class Message ( id : Long, country : String, dis : Double) extends Serializable {
      var id_ : Long = id
      var country_ : String = country
      var dis_ : Double  = dis

      def this()  {
        this(0,"",0.0)
      }

      override def toString: String = this.id_ +" "+ this.country_ +" "+ this.dis_
    }

    val newGraph = graph.mapVertices[(VertexData,Message)]( (x,data) => (data,new Message()) )

    // Gathering the attributes of the neighbors to compute the vote
    val messagesStep1 = graph.aggregateMessages[Message](context => {
      val dis: Double = countryDistance(context.srcAttr.country_, context.dstAttr.country_)
      if(dis > 0.5) {
        context.sendToSrc(new Message(context.dstId, context.dstAttr.country_, dis))
        context.sendToDst(new Message(context.srcId, context.srcAttr.country_, dis))
      }
    }, (a, b) => (a.dis_ > b.dis_) match {
      case true => a
      case false => a.dis_ == b.dis_ match {
        case false => b
        case true => a.id_ < b.id_ match {
          case true => a
          case false => b
        }
      }
    }, new TripletFields(true, true, true)
    )

    val step1graph : Graph[(VertexData,Message),Int] = newGraph.joinVertices[Message](messagesStep1)((id, data, message) => (data._1, message))

    // Sending the votes to the corresponding neighbors and take the decision based on all the observed votes
    val messagesStep2 = step1graph.aggregateMessages[(Int,Message)](context => {
      if (context.srcId == context.dstAttr._2.id_) context.sendToSrc((1,new Message(context.dstId, context.dstAttr._1.country_, context.dstAttr._2.dis_)))
      if (context.dstId == context.srcAttr._2.id_) context.sendToDst((1,new Message(context.srcId, context.srcAttr._1.country_, context.srcAttr._2.dis_)))
    }, (a, b) => a._2.dis_ > b._2.dis_ match {
      case true => (a._1 + b._1, a._2)
      case false => (a._1 + b._1, b._2)
    }, new TripletFields(true, true, true))

    var step2Graph : Graph[(VertexData,Message),Int] = step1graph.joinVertices[(Int, Message)](messagesStep2)((id, data, message) => {
      message._1 > (data._1.degree_)/2 match {
        case true => (new VertexData(data._1.degree_, data._1.country_, true), message._2)
        case false => (new VertexData(data._1.degree_, data._1.country_, false), message._2)
      }
    })

    // Perform the attribute reassignment
    val toReplace = step2Graph.vertices.filter( x => x._2._1.toReplace_ )
    println("Number of vertices to move: "+toReplace.count)

    val verticespercountry = toReplace.sortBy(key => key._2._1.country_).zipWithIndex.map[(Long, (VertexId, VertexData))](a => (a._2, (a._1._1, a._1._2._1)))
    val verticesperrequired = toReplace.sortBy(key => key._2._2.country_).zipWithIndex.map[(Long, (VertexId, VertexData))](a => (a._2, (a._1._1, a._1._2._1)))
    val joined: RDD[(VertexId, VertexData)] = verticespercountry.join(verticesperrequired).map[(VertexId, VertexData)](row => {
      val first = row._2._1
      val second = row._2._2
      (first._1, second._2)
    })


    var finalGraph : Graph[VertexData,Int] = graph.joinVertices[VertexData](joined)((id, data, newAttr) =>
      new VertexData(data.degree_, newAttr.country_,false)
    )

    //(finalGraph, computeHomophily(graph))
    (finalGraph, 0.0)
  }
  */

  def readCountryCoordinates( sparkContext : SparkContext) = {
    val coordinates =  scala.io.Source.fromFile("/home/aprat/projects/oracle/datageneration/dicLocations.txt").getLines.map(line => line.split(" ")).map( line => (line(1),line(2), line(3)))
    coordinates.foreach( entry => countryPosition += ( entry._1 -> (Integer.parseInt(entry._2),Integer.parseInt(entry._3))))
  }

  def main(args:Array[String]) : Unit = {

    val spark = SparkSession.builder ()
      .appName ("Datasynth Spark Schnappi Interpreter")
      .master ("local[4]")
      .getOrCreate ()

    spark.sparkContext.setLogLevel("ERROR");

    println("Reading coordinates")
    readCountryCoordinates(spark.sparkContext)

    println((new VertexAttributes("China","University_of_China",45)).distance(new VertexAttributes("Spain","University_of_Granada",45)))

    println("Building graph")
    val attributes = spark.sparkContext.textFile ("/home/aprat/projects/oracle/datageneration/attributes.csv").map (line => line.split (",") ).map (line => (line (0),line(2), Integer.parseInt(line(1))));
    val vertexIds: Array[Long] = Array.range (1, 10001).map (i => i.toLong)

    val vertices = vertexIds zip attributes.collect
    val edges = spark.sparkContext.textFile ("/home/aprat/projects/oracle/datageneration/network.dat").map (line => line.split ("\t") ).map (line => new Edge (line (0).toInt, line (1).toInt, 0) ).collect ()
    val verticesRDD: RDD[(Long, (String,String,Int))] = spark.sparkContext.parallelize (vertices)
    val edgesRDD: RDD[Edge[Int]] = spark.sparkContext.parallelize (edges)

    /** Setting the degree of the vertex as attribute to each vertex **/
    var graph: Graph[VertexData,Int] = Graph (verticesRDD, edgesRDD).removeSelfEdges.mapVertices[VertexData] ((id, s) => new VertexData(0,false,new VertexAttributes(s._1,s._2,s._3)))
    graph = graph.joinVertices[Int](graph.degrees)( (id,data,degree) => new VertexData(degree,data.toReplace_,data.vertexAttributes_))

    println("Computing homophily when random assignment")
    val randomHomophily : Double = computeHomophily(graph)

    /*println("Computing initial solution based on communities")
    val labeledgraph = LabelPropagation.run[VertexData,Int](graph, 20)

    var numCommunities = labeledgraph.vertices.map[VertexId]( x => x._2).distinct.count

    println("Number of communities in the graph: "+numCommunities)

    val sortedAttributes = attributes.sortBy[String]( s => s).zipWithIndex.map[(Long,String)]( a => (a._2, a._1))
    val newVertices = labeledgraph.vertices.sortBy[VertexId]( x => x._2 ).zipWithIndex.map[(Long, (VertexId,VertexId))]( a => (a._2,(a._1._1,a._1._2))).join(sortedAttributes).map[(VertexId,(VertexId,String))]( a => (a._2._1._1, (a._2._1._2, a._2._2)))
    graph = graph.joinVertices[(VertexId,String)](newVertices)( (id,data,s) => {
      new VertexData(data.degree_, s._2, data.toReplace_)
    })

    //Saving initial graph to file
    graph.vertices.coalesce(1).saveAsTextFile("data/vertices_initial.dat")

    println("Computing homophily of initial partition")
    val initialHomophily : Double = computeHomophily(graph)

    for( i <- 1 to 10)
    {
      println("Starting OneStepBullying iteration "+i)
      var tuple : (Graph[VertexData, Int], Double) = oneStepBullying(graph)
      graph = tuple._1
    }
    */

    graph.vertices.coalesce(1).saveAsTextFile("data/vertices_final.dat")
    val finalHomophily : Double = computeHomophily(graph)
    println("Random Homophily: "+(randomHomophily))
    //println("Initial Homophily: "+(1.0 - initialHomophily))
    println("Final Homophily: "+ (finalHomophily))
  }
}
