/**
  * Created by aprat on 17/10/16.
  */
package org.dama.datasynth.tests

import java.util.logging.{Level, LogManager, Logger}

import org.apache.spark.Partitioner
import org.apache.spark.graphx._
import org.apache.spark.graphx.lib.LabelPropagation
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object Homophily {



  @SerialVersionUID(100L)
  class VertexData ( degree : Int, country : String, toReplace : Boolean ) extends Serializable{
    var degree_ = degree
    var country_ : String = country
    var toReplace_ : Boolean = toReplace

    def this()  {
      this(0,"",false)
    }

    override def toString: String = country_
  }

  def distance(country1: String, country2: String): Double = {
    country1.compareTo(country2) match {
      case 0 => 0.0
      case _ => 1.0
    }
  }

  def computeHomophily( graph : Graph[VertexData,Int] ) : Double = {
    class HomophilyData( count : Int, dis : Double) extends Serializable {
      val count_ = count
      val dis_ = dis
    }

    val messages = graph.aggregateMessages[HomophilyData]( context => {
      val dis = distance(context.srcAttr.country_,context.dstAttr.country_)
      context.sendToDst(new HomophilyData(1,dis))
      context.sendToSrc(new HomophilyData(1,dis))
    }, (a,b) => {
      val count = a.count_ + b.count_
      new HomophilyData(count, (a.count_ * a.dis_ + b.count_ * b.dis_)/count)
    })

    val sum : Double = messages.reduce( (a,b) => (1,new HomophilyData(1,a._2.dis_ + b._2.dis_)))._2.dis_
    return sum/messages.count()
  }

  def oneStepBullying( graph : Graph[VertexData,Int]) : (Graph[VertexData,Int],Double) = {
    val neighborCountries = graph.mapVertices[(VertexData,Map[String,Int])]( (id,data) => (data,Map())).aggregateMessages[Map[String,Int]](
      context => {
        context.sendToDst(Map(context.srcAttr._1.country_ -> 1))
        context.sendToSrc(Map(context.dstAttr._1.country_ -> 1))
      },
      (count1, count2) => (count1.keySet ++ count2.keySet).map { i =>
        val countVal1 = count1.getOrElse(i,0)
        val countVal2 = count2.getOrElse(i,0)
        i -> (countVal1 + countVal2)
      }.toMap
    ).map[(VertexId,(String,Int))]( node => {
      (node._1, node._2.toList.reduce[(String,Int)]( (x,y) => {
        if(x._2 < y._2) x else y
      }))
    })

    val stepGraph  = graph.mapVertices[(VertexData,String)]( (id,data) => (data,"")).joinVertices[(String,Int)](neighborCountries)( (id, data, country) => {
      data._1.toReplace_ = (country._2 / data._1.degree_ > 0.5)
      (data._1,country._1)
    })

    // Perform the attribute reassignment
    val toReplace = stepGraph.vertices.filter( x => x._2._1.toReplace_ )
    println("Number of vertices to move: "+toReplace.count)

    val verticespercountry = toReplace.sortBy(key => key._2._1.country_).zipWithIndex.map[(Long, (VertexId, VertexData))](a => (a._2, (a._1._1, a._1._2._1)))
    val verticesperrequired = toReplace.sortBy(key => key._2._2).zipWithIndex.map[(Long, (VertexId, VertexData))](a => (a._2, (a._1._1, a._1._2._1)))
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
      val dis: Double = distance(context.srcAttr.country_, context.dstAttr.country_)
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

  def main(args:Array[String]) : Unit = {


    val spark = SparkSession.builder ()
      .appName ("Datasynth Spark Schnappi Interpreter")
      .master ("local[4]")
      .getOrCreate ()

    spark.sparkContext.setLogLevel("ERROR");

    println("Building graph")
    val attributes = spark.sparkContext.textFile ("/home/aprat/projects/oracle/datageneration/attributes.csv").map (line => line.split (",") ).map (line => line (0) )
    val vertexIds: Array[Long] = Array.range (1, 10001).map (i => i.toLong)

    val vertices = vertexIds zip attributes.collect
    val edges = spark.sparkContext.textFile ("/home/aprat/projects/oracle/datageneration/network.dat").map (line => line.split ("\t") ).map (line => new Edge (line (0).toInt, line (1).toInt, 0) ).collect ()
    val verticesRDD: RDD[(Long, String)] = spark.sparkContext.parallelize (vertices)
    val edgesRDD: RDD[Edge[Int]] = spark.sparkContext.parallelize (edges)

    /** Setting the degree of the vertex as attribute to each vertex **/
    var graph: Graph[VertexData,Int] = Graph (verticesRDD, edgesRDD).removeSelfEdges.mapVertices[VertexData] ((id, s) => new VertexData(0,s,false) )
    graph = graph.joinVertices[Int](graph.degrees)( (id,data,degree) => new VertexData(degree,data.country_,data.toReplace_))

 //   graph.vertices.collect().sortBy[VertexId]( x => x._1 ).foreach(println)

    println("Computing homophily when random assignment")
    val randomHomophily : Double = computeHomophily(graph)

    println("Computing initial solution based on communities")
    val labeledgraph = LabelPropagation.run[VertexData,Int](graph, 20)

    var numCommunities = labeledgraph.vertices.map[VertexId]( x => x._2).distinct.count

    println("Number of communities in the graph: "+numCommunities)

    val sortedAttributes = attributes.sortBy[String]( s => s).zipWithIndex.map[(Long,String)]( a => (a._2, a._1))
    val newVertices = labeledgraph.vertices.sortBy[VertexId]( x => x._2 ).zipWithIndex.map[(Long, (VertexId,VertexId))]( a => (a._2,(a._1._1,a._1._2))).join(sortedAttributes).map[(VertexId,(VertexId,String))]( a => (a._2._1._1, (a._2._1._2, a._2._2)))
    graph = graph.joinVertices[(VertexId,String)](newVertices)( (id,data,s) => {
      new VertexData(data.degree_, s._2, data.toReplace_)
    })

//    graph.vertices.collect().sortBy[VertexId]( x => x._1 ).foreach(println)

    //Saving initial graph to file
    graph.vertices.coalesce(1).saveAsTextFile("data/vertices_initial.dat")

    println("Computing homophily of initial partition")
    val initialHomophily : Double = computeHomophily(graph)

    for( i <- 1 to 40)
    {
      println("Starting OneStepBullying iteration "+i)
      var tuple : (Graph[VertexData, Int], Double) = oneStepBullying(graph)
      graph = tuple._1
    }

    for( i <- 1 to 40)
    {
      println("Starting TwoStepBullying iteration "+i)
      var tuple : (Graph[VertexData, Int], Double) = twoStepBullying(graph)
      graph = tuple._1
    }

    graph.vertices.coalesce(1).saveAsTextFile("data/vertices_final.dat")
    val finalHomophily : Double = computeHomophily(graph)
    println("Random Homophily: "+(1.0 - randomHomophily))
    println("Initial Homophily: "+(1.0 - initialHomophily))
    println("Final Homophily: "+ (1.0 - finalHomophily))
  }
}
