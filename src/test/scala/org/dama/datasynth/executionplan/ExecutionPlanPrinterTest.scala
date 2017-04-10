package org.dama.datasynth.executionplan

import org.dama.datasynth.executionplan.ExecutionPlan._
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.junit.JUnitRunner

/**
  * Created by aprat on 6/04/17.
  */
@RunWith(classOf[JUnitRunner])
class ExecutionPlanPrinterTest extends FlatSpec with Matchers {

  "An ExecutionPlanPrinter printExecutionPlan" should "output like " +
    "[CreatePropertyTable,typeName.propertyName]\n" +
    "    [StaticValue[Long],1000]\n" +
    "    [PropertyGenerator,propertyGeneratorName]\n" +
    "        [StaticValue[Long],10000]\n" in {
    val executionPlanPrinter = new ExecutionPlanPrinter()
    val longParameter10000 = StaticValue[Long](10000)
    val longParameter1000 = StaticValue[Long](1000)
    val propertyGenerator = PropertyGenerator("propertyGeneratorName",List(longParameter10000), Seq())
    val createPropertyTable = CreatePropertyTable("typeName","propertyName",propertyGenerator,longParameter1000)
    executionPlanPrinter.printExecutionPlan(createPropertyTable) should be ("[CreatePropertyTable,typeName.propertyName]\n" +
    "    [StaticValue[Long],1000]\n" +
    "    [PropertyGenerator,propertyGeneratorName]\n" +
    "        [StaticValue[Long],10000]\n")
  }

}
