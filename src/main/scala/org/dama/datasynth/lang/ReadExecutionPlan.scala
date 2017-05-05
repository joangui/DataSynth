package org.dama.datasynth.lang

import net.liftweb.json._
import org.dama.datasynth.LoadPropertyTables
import org.dama.datasynth.executionplan.ExecutionPlan.{ExecutionPlanNode, _}
import org.dama.datasynth.schema.{Schema}

import scala.reflect.runtime.universe._
import scala.collection.mutable
/**
  * Created by joangui on 13/04/2017.
  * ReadExecutionPlan is used to read a json and create the schema of the graph to be generated.
  * It also allows to create the necessary Tables, as ExecutionPlanNodes, that are necessary by the
  * runtime to generate the graph.
  */

object ReadExecutionPlan {

  var schema:Schema = null
  /**
  * Load a schema
  * @param json String containin the definition of the schema in JSON format.
  * @return Schema
  */
 def loadSchema(json : String): Schema ={
   implicit val formats = DefaultFormats


   val jsonT = parse(json)
   schema = jsonT.extract[Schema]
   schema
 }



  /**
    * Given a schema return the set of Tables necessary to create it.
    * @param schema to be created
    * @return the sequence of necessary Tables to create the graph.
    */
  def createExecutionPlan(schema: Schema):Seq[Table]=
  {
    val propertyTablesNodes:Seq[PropertyTable[_]] = LoadPropertyTables.getPropertyTableNodes(schema.nodeTypes)
    val edgeTablesNodes:Seq[Table] = LoadStructuralTables.getStructuralTables(schema,propertyTablesNodes)
    propertyTablesNodes++edgeTablesNodes
  }

  /**
    *
    * @param initParameters A sequence in the form {value:dataType}
    * @return a sequence of values
    */
  def readInitParameters(initParameters: Seq[String]):Seq[Value[_]] = {
    initParameters.map(initParameter=> {
     val keyValueSeq = initParameter.split(":")
     keyValueSeq(1).toLowerCase match {
      case "string" =>  StaticValue[String](keyValueSeq(0))
      case "int" =>  StaticValue[Int](keyValueSeq(0).toInt)
      case "double" =>  StaticValue[Double](keyValueSeq(0).toDouble)
      case "long" =>  StaticValue[Long](keyValueSeq(0).toLong)
      case "float" =>  StaticValue[Float](keyValueSeq(0).toFloat)
     }
    })

  }



}


