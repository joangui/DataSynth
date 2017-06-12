package org.dama.datasynth.lang

import com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDateTimeException
import org.dama.datasynth.executionplan.ExecutionPlan.{PropertyGenerator, Value}
import org.dama.datasynth.lang.ReadExecutionPlan.loadSchema
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.junit.JUnitRunner
import org.dama.datasynth.schema.Schema

import scala.io.Source
import scala.util.control.NonFatal
/**
  * Created by joangui on 13/04/2017.
  */
@RunWith(classOf[JUnitRunner])
class ReadExecutionPlanTest extends FlatSpec with Matchers {
  /** Generator tests **/

  def tryCatch (json : String): Unit =
  {
    try {
      val schema: Schema = ReadExecutionPlan.loadSchema(json)
      false should be(true)
    }
    catch {
      case NonFatal(e:Exception) =>
        e.printStackTrace()
        true should be(true)
      case NonFatal(e) =>
        e.printStackTrace()
        false should be (true)
    }
  }
  "A ReadExecutionPlan size" should "output like [numNodeTypes]" in {
    val json : String = Source.fromFile("./src/test/resources/simpleQuery.json").getLines.mkString
    val schema: Schema = ReadExecutionPlan.loadSchema(json)
    schema.nodeTypes.size should be (2)
  }

  "A ReadExecutionPlan nodeTypes" should "output like seq[numNodeTypes]" in {
    val json : String = Source.fromFile("./src/test/resources/simpleQuery.json").getLines.mkString
    val schema: Schema = ReadExecutionPlan.loadSchema(json)
    val names = schema.nodeTypes.map(entityNode => entityNode.name)
                                .sortWith((nameA,nameB)=>nameA.compareTo(nameB)<0)
    val gtNames = List("message","person")
    for ( (m, a) <- (names zip gtNames)) m should be (a)
  }

  "A ReadExecutionPlan properties for person," should "output like [amount]" in {
    val json : String = Source.fromFile("./src/test/resources/simpleQuery.json").getLines.mkString
    val schema: Schema = ReadExecutionPlan.loadSchema(json)
    val node =  schema.nodeTypes.find(nodeType => nodeType.name.equals("person")).foreach(node =>
    node.properties.foreach(properties=> properties.size should be (2)))
  }
  "A ReadExecutionPlan properties for person," should "output like [nodeType1.property1,nodeType1.property1]" in {
    val json : String = Source.fromFile("./src/test/resources/simpleQuery.json").getLines.mkString
    val schema: Schema = ReadExecutionPlan.loadSchema(json)
    schema.nodeTypes.find(nodeType => nodeType.name.equals("person")).foreach(node=>{
      node.properties.foreach(properties=>{
        val names= properties.map(entityNode => entityNode.name).sortWith((nameA,nameB)=>nameA.compareTo(nameB)<0)
        val gtNames = List("country","sex")
        for ( (m, a) <- (names zip gtNames)) m should be (a)})})
  }

  "A ReadExecutionPlan properties detail for person," should "output like P[country,String,G[A,List(sex),List(C,D)]]"  in {
    val json : String = Source.fromFile("./src/test/resources/simpleQuery.json").getLines.mkString
    val schema: Schema = ReadExecutionPlan.loadSchema(json)
      schema.nodeTypes.find(nodeType => nodeType.name.equals("person")).foreach(node=> {
          node.properties.foreach(propeties=>propeties.find(x => x.name=="country").foreach(property =>
          property.toString should be ("P[country,String,G[A,List(sex),List(C:String, D:Long)]]")))})
  }
  "A ReadExecutionPlan edgeType" should "output like [numEdgeType]" in {
    val json : String = Source.fromFile("./src/test/resources/simpleQuery.json").getLines.mkString
    val schema: Schema = ReadExecutionPlan.loadSchema(json)
   val edgeTypes = schema.edgeTypes
    edgeTypes.size should be (1)
  }
  "A ReadExecutionPlan edgeType toString" should "output like ET[likes,person,person,S[BTER,List(E:String, F:Long)],C[country,country,/path/]]" in {
    val json : String = Source.fromFile("./src/test/resources/simpleQuery.json").getLines.mkString
    val schema: Schema = ReadExecutionPlan.loadSchema(json)
    val edgeTypes = schema.edgeTypes
    val edgeType = edgeTypes(0)
    s"$edgeType" should be ("ET[likes,person,person,S[BTER,List(E:String, F:Long)],C[country,country,/path/]]")
  }

  "A ReadExecutionPlan validSchema" should "output like true" in{
    val json : String = Source.fromFile("./src/test/resources/simpleQuery.json").getLines.mkString
    val schema: Schema = ReadExecutionPlan.loadSchema(json)

  }
  "A ReadExecutionPlan validSchema nodeDependencies" should "output like false" in{
    val json : String = Source.fromFile("./src/test/resources/incorrectSimpleQuery.json").getLines.mkString
    tryCatch(json)

  }

  "A ReadExecutionPlan validSchema edge corellations" should "output like false" in{
    val json : String = Source.fromFile("./src/test/resources/incorrectSimpleQuery2.json").getLines.mkString
    tryCatch(json)

  }

  "A ReadExecutionPlan validSchema uniqueness types" should "output like false" in{
    val json : String = Source.fromFile("./src/test/resources/incorrectSimpleQuery3.json").getLines.mkString
    tryCatch(json)

  }

  "A ReadExecutionPlan validSchema uniqueness node properties " should "output like false" in{
    val json : String = Source.fromFile("./src/test/resources/incorrectSimpleQuery4.json").getLines.mkString
    tryCatch(json)

  }
  "A ReadExecutionPlan validSchema valid edges" should "output like false" in{
    val json : String = Source.fromFile("./src/test/resources/incorrectSimpleQuery5.json").getLines.mkString
    tryCatch(json)

  }
  "A ReadExecutionPlan validSchema valid cyclic dependencies" should "output like false" in{
    val json : String = Source.fromFile("./src/test/resources/cyclicDependencies.json").getLines.mkString
    tryCatch(json)
  }

}
