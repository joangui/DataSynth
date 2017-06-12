package org.dama.datasynth.runtime.spark.passes

import org.apache.spark.sql.SparkSession
import org.dama.datasynth.DataSynthConfig
import org.dama.datasynth.executionplan.ExecutionPlan
import org.dama.datasynth.executionplan.ExecutionPlan.{EdgeTable, PropertyTable, StaticValue, TableSize}
import org.dama.datasynth.runtime.spark.operators.TableSizeOperator
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by aprat on 31/05/17.
  */
class RuntimePropertyGeneratorBuilderTest extends FlatSpec with Matchers {

  "The source code for a boolean runtime property generator " should " be correctly generated " in {
    val value = ExecutionPlan.StaticValue[Boolean](true)
    val generator = ExecutionPlan.PropertyGenerator[Boolean]("org.dama.datasynth.common.generators.property.dummy.DummyBooleanPropertyGenerator",Seq(value),Seq())

    val config = DataSynthConfig()
    val runtimePropertyGeneratorBuilder = new RuntimePropertyGeneratorBuilder(config)
    val generatedSource : String = runtimePropertyGeneratorBuilder.generatePGClassDefinition("boolean.property",generator)
    generatedSource.trim() should be (
      s"import org.dama.datasynth.runtime.spark.utils._\n" +
      s"import org.dama.datasynth.common.generators.property._\n" +
      s"class BOOLEANPROPERTY extends PropertyGenerator[Boolean] {\n" +
      s"val generatorBOOLEANPROPERTY = new org.dama.datasynth.common.generators.property.dummy.DummyBooleanPropertyGenerator(true)\n"+
      s" def run(id : Long, random : Long, dependencies : Any*) : Boolean = generatorBOOLEANPROPERTY.run(id,random)\n" +
      s"}\n".trim()
    )
  }

  "The source code for a boolean runtime property generator with a dependency " should " be correctly generated " in {

    val valueFloat = ExecutionPlan.StaticValue[Float](1.0f)
    val generatorFloat = ExecutionPlan.PropertyGenerator[Float]("org.dama.datasynth.common.generators.property.dummy.DummyFloatPropertyGenerator",Seq(valueFloat),Seq())
    val size = ExecutionPlan.StaticValue[Long](1000)
    val tableFloat = PropertyTable[Float]("float","property",generatorFloat,size)

    val valueBoolean = ExecutionPlan.StaticValue[Boolean](true)
    val generatorBoolean = ExecutionPlan.PropertyGenerator[Boolean]("org.dama.datasynth.common.generators.property.dummy.DummyBooleanPropertyGenerator",Seq(valueBoolean),Seq(tableFloat))

    val config = DataSynthConfig()
    val runtimePropertyGeneratorBuilder = new RuntimePropertyGeneratorBuilder(config)
    val generatedSource : String = runtimePropertyGeneratorBuilder.generatePGClassDefinition("boolean.property",generatorBoolean)
    generatedSource.trim() should be (
      s"import org.dama.datasynth.runtime.spark.utils._\n" +
        s"import org.dama.datasynth.common.generators.property._\n" +
        s"class BOOLEANPROPERTY extends PropertyGenerator[Boolean] {\n" +
        s"val generatorBOOLEANPROPERTY = new org.dama.datasynth.common.generators.property.dummy.DummyBooleanPropertyGenerator(true)\n"+
        s" def run(id : Long, random : Long, dependencies : Any*) : Boolean = generatorBOOLEANPROPERTY.run(id,random,dependencies(0) match { case value : Float => value })\n" +
        s"}\n".trim()
    )
  }
}
