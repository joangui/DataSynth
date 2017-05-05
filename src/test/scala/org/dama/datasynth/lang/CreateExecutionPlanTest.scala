package org.dama.datasynth.lang

import org.dama.datasynth.executionplan.ExecutionPlan.Table
import org.dama.datasynth.schema.Schema
import org.junit.runner
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

/**
  * Created by joangui on 13/04/2017.
  */
@RunWith(classOf[JUnitRunner])
class CreateExecutionPlanTest extends FlatSpec with Matchers {
  /** Generator tests **/
  "A ReadExecutionPlan createExectuionPlan propertyTablesNodes.size " should "output like 3" in{
    val json : String = Source.fromFile("./src/test/resources/propertyTableTest.json").getLines.mkString
    val schema: Schema = ReadExecutionPlan.loadSchema(json)
    val executionPlanNodes:Seq[Table]=ReadExecutionPlan.createExecutionPlan(schema)

    executionPlanNodes.size should be (3)

  }
  "A ReadExecutionPlan createExectuionPlan voidList.isEmpty  " should "output like true" in{
    val json : String = Source.fromFile("./src/test/resources/propertyTableTest.json").getLines.mkString
    val schema: Schema = ReadExecutionPlan.loadSchema(json)
    val executionPlanNodes:Seq[Table]=ReadExecutionPlan.createExecutionPlan(schema)

    val PropertyTables:Seq[String] = Seq("[PropertyTable,person.sex]","[PropertyTable,person.country]","[PropertyTable,person.name]")

    val voidList = executionPlanNodes.filter(node=>{!PropertyTables.contains(node.toString)})


    voidList.isEmpty should be (true)

  }


  "A ReadExecutionPlan createExectuionPlan propertyTablesNodes.size " should "output like 5" in{
    val json : String = Source.fromFile("./src/test/resources/propertyTableTest2.json").getLines.mkString
    val schema: Schema = ReadExecutionPlan.loadSchema(json)
    val executionPlanNodes:Seq[Table]=ReadExecutionPlan.createExecutionPlan(schema)

    executionPlanNodes.size should be (5)

  }


  "A ReadExecutionPlan createExectuionPlan with edges propertyTablesNodes.size " should "output like 3" in{
    val json : String = Source.fromFile("./src/test/resources/edgeTableTest.json").getLines.mkString
    val schema: Schema = ReadExecutionPlan.loadSchema(json)
    val executionPlanNodes:Seq[Table]=ReadExecutionPlan.createExecutionPlan(schema)

    executionPlanNodes.size should be (4)

  }
  "A ReadExecutionPlan createExectuionPlan with edges and correlation propertyTablesNodes.size " should "output like 5" in{
    val json : String = Source.fromFile("./src/test/resources/edgeTableTest2.json").getLines.mkString
    val schema: Schema = ReadExecutionPlan.loadSchema(json)
    val executionPlanNodes:Seq[Table]=ReadExecutionPlan.createExecutionPlan(schema)

    executionPlanNodes.size should be (5)

  }



}
