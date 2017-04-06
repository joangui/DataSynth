package org.dama.datasynth.executionplan

import org.dama.datasynth.executionplan.ExecutionPlan.{CreatePropertyTable, LongParameter, PropertyGenerator}
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.junit.JUnitRunner

/**
  * Created by aprat on 6/04/17.
  */
@RunWith(classOf[JUnitRunner])
class ExecutionPlanPrinterTest extends FlatSpec with Matchers {

  "An ExecutionPlanPrinter printExecutionPlan" should "output like " +
    "[CreatePropertyTable,tableName]\n" +
    "    [LongParameter,1000]\n" +
    "    [PropertyGenerator,propertyGeneratorName]\n" +
    "        [LongParameter,10000]\n" in {

    val executionPlanPrinter = new ExecutionPlanPrinter()
    val longParameter10000 = LongParameter(10000)
    val longParameter1000 = LongParameter(1000)
    val propertyGenerator = PropertyGenerator("propertyGeneratorName",List(longParameter10000), Seq())
    val createPropertyTable = CreatePropertyTable("tableName",propertyGenerator,longParameter1000)
    executionPlanPrinter.printExecutionPlan(createPropertyTable) should be ("[CreatePropertyTable,tableName]\n" +
    "    [LongParameter,1000]\n" +
    "    [PropertyGenerator,propertyGeneratorName]\n" +
    "        [LongParameter,10000]\n")
  }

}
