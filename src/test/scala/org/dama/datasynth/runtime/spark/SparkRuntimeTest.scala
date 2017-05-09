package org.dama.datasynth.runtime.spark

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.SparkSession
import org.dama.datasynth.executionplan.ExecutionPlan
import org.dama.datasynth.executionplan.ExecutionPlan.PropertyTable
import org.dama.datasynth.runtime.spark.operators.FetchTableOperator
import org.dama.datasynth.runtime.spark.utils.DataSynthConfig
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers, Suite}

/**
  * Created by aprat on 11/04/17.
  */
@RunWith(classOf[JUnitRunner])
class SparkRuntimeTest extends FlatSpec with Matchers with BeforeAndAfterAll {

  SparkSession
    .builder()
    .master("local[*]")
    .getOrCreate()

  val config = DataSynthConfig().outputDir("/tmp/datasynth")

  " A boolean table " should " contain all true " in {
    val value = ExecutionPlan.StaticValue[Boolean](true)
    val generator = ExecutionPlan.PropertyGenerator[Boolean]("org.dama.datasynth.common.generators.property.dummy.DummyBooleanPropertyGenerator",Seq(value),Seq())
    val size = ExecutionPlan.StaticValue[Long](1000)
    val createPropertyTable = PropertyTable[Boolean]("boolean","property",generator,size)
    SparkRuntime.run( config, Seq(createPropertyTable))
    FetchTableOperator.booleanTables.get("boolean.property").get.collect.foreach(t => t._2 should be (value.value))
  }

  " A float table " should " contain all 1.0 " in {
    val value = ExecutionPlan.StaticValue[Float](1.0f)
    val generator = ExecutionPlan.PropertyGenerator[Float]("org.dama.datasynth.common.generators.property.dummy.DummyFloatPropertyGenerator",Seq(value),Seq())
    val size = ExecutionPlan.StaticValue[Long](1000)
    val createPropertyTable = PropertyTable[Float]("float","property",generator,size)
    SparkRuntime.run( config, Seq(createPropertyTable))
    FetchTableOperator.floatTables.get("float.property").get.collect.foreach( t => t._2 should be (value.value))
  }

  " A double table " should " contain all 1.0 " in {
    val value = ExecutionPlan.StaticValue[Double](1.0)
    val generator = ExecutionPlan.PropertyGenerator[Double]("org.dama.datasynth.common.generators.property.dummy.DummyDoublePropertyGenerator",Seq(value),Seq())
    val size = ExecutionPlan.StaticValue[Long](1000)
    val createPropertyTable = PropertyTable[Double]("double","property",generator,size)
    SparkRuntime.run( config, Seq(createPropertyTable))
    FetchTableOperator.doubleTables.get("double.property").get.collect.foreach( t => t._2 should be (value.value))
  }

  " A long table " should " contain all 1s " in {
    val num = ExecutionPlan.StaticValue[Long](1)
    val generator = ExecutionPlan.PropertyGenerator[Long]("org.dama.datasynth.common.generators.property.dummy.DummyLongPropertyGenerator",Seq(num),Seq())
    val size = ExecutionPlan.StaticValue[Long](1000)
    val createPropertyTable = PropertyTable[Long]("long","property",generator,size)
    SparkRuntime.run( config, Seq(createPropertyTable))
    FetchTableOperator.longTables.get("long.property").get.collect.foreach( t => t._2 should be (num.value))
  }

  " An int table " should " contain all 1s " in {
    val num = ExecutionPlan.StaticValue[Int](1)
    val generator = ExecutionPlan.PropertyGenerator[Int]("org.dama.datasynth.common.generators.property.dummy.DummyIntPropertyGenerator",Seq(num),Seq())
    val size = ExecutionPlan.StaticValue[Long](1000)
    val createPropertyTable = PropertyTable[Int]("int","property",generator,size)
    SparkRuntime.run( config, Seq(createPropertyTable))
    FetchTableOperator.intTables.get("int.property").get.collect.foreach( t => t._2 should be (num.value))
  }

  " A property table created with a dummyLongMultPropertyGenerator" should "contain the result of multiplying two long tables" in {
    val size = ExecutionPlan.StaticValue[Long](1000)
    val num1 = ExecutionPlan.StaticValue[Long](2)
    val generator1 = ExecutionPlan.PropertyGenerator[Long]("org.dama.datasynth.common.generators.property.dummy.DummyLongPropertyGenerator",Seq(num1),Seq())
    val propertyTable1 = ExecutionPlan.PropertyTable[Long]("long","property1", generator1, size)

    val num2 = ExecutionPlan.StaticValue[Long](3)
    val generator2 = ExecutionPlan.PropertyGenerator[Long]("org.dama.datasynth.common.generators.property.dummy.DummyLongPropertyGenerator",Seq(num2),Seq())
    val propertyTable2 = ExecutionPlan.PropertyTable[Long]("long","property2", generator2, size)

    val generator3 = ExecutionPlan.PropertyGenerator[Long]("org.dama.datasynth.common.generators.property.dummy.DummyLongMultPropertyGenerator",Seq(),Seq(propertyTable1, propertyTable2))
    val propertyTable3 = ExecutionPlan.PropertyTable[Long]("long","property3", generator3, size)
    SparkRuntime.run( config, Seq(propertyTable1, propertyTable2, propertyTable3))
    FetchTableOperator.longTables.get("long.property3").get.collect.foreach( t => t._2 should be (6))
  }

  override def afterAll(): Unit = {
    val fileSystem = FileSystem.get(new Configuration())
    fileSystem.delete( new Path(config.outputDir), true)
  }
}
