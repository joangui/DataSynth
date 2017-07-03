package org.dama.datasynth.runtime.spark.passes

import org.dama.datasynth.executionplan.ExecutionPlan
import org.dama.datasynth.executionplan.ExecutionPlan.PropertyTable
import org.scalatest.{FlatSpec, Matchers}
import scala.reflect.runtime.universe.{TypeTag, typeOf}

/**
  * Created by aprat on 31/05/17.
  */
class InjectRuntimeGeneratorsTest extends FlatSpec with Matchers {

  "The source code for a boolean runtime property generator with a dependency " should " be correctly generated " in {

    val size = ExecutionPlan.StaticValue[Long](1000)
    val valueFloat = ExecutionPlan.StaticValue[Float](1.0f)
    val generatorFloat = ExecutionPlan.PropertyGenerator[Float]("org.dama.datasynth.common.generators.property.dummy.DummyFloatPropertyGenerator",Seq(valueFloat),Seq())
    val tableFloat = PropertyTable[Float]("float","property",generatorFloat,size)

    val valueBoolean = ExecutionPlan.StaticValue[Boolean](true)
    val generatorBoolean = ExecutionPlan.PropertyGenerator[Boolean]("org.dama.datasynth.common.generators.property.dummy.DummyBooleanPropertyGenerator",Seq(valueBoolean),Seq(tableFloat))
    val tableBoolean = PropertyTable[Boolean]("boolean","property",generatorBoolean,size)

    val generatorMap : Map[String,String] = Map( "boolean.property" -> "BOOLEANPROPERTY", "float.property" -> "FLOATPROPERTY")
    val injectRuntimeGenerators = new InjectRuntimeGenerators(generatorMap)
    val executionPlan = injectRuntimeGenerators.run(Seq(tableBoolean,tableFloat))
    executionPlan(0) match {
      case table : PropertyTable[Boolean@unchecked] if table.tag.tpe =:= typeOf[Boolean] => table.generator.className should be ("BOOLEANPROPERTY")
      case _ => throw new RuntimeException("Table is not of type PropertyTable[Boolean]")
    }

    executionPlan(1) match {
      case table : PropertyTable[Float@unchecked] if table.tag.tpe =:= typeOf[Float] => table.generator.className should be ("FLOATPROPERTY")
      case _ => throw new RuntimeException("Table is not of type PropertyTable[Float]")
    }
  }
}
