package org.dama.datasynth.runtime.spark.operators

import org.apache.spark.sql.SparkSession
import org.dama.datasynth.executionplan.ExecutionPlan
import org.dama.datasynth.executionplan.ExecutionPlan.{EdgeTable, PropertyTable, StaticValue, TableSize}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by aprat on 12/04/17.
  */
@RunWith(classOf[JUnitRunner])
class OperatorsTest extends FlatSpec with Matchers {

  SparkSession
    .builder()
    .master("local[*]")
    .getOrCreate()

  " An TableSizeOperator on a table of size 1000" should " should return 1000 " in {
    val num = ExecutionPlan.StaticValue[Int](1)
    val generator = ExecutionPlan.PropertyGenerator[Int]("org.dama.datasynth.common.generators.property.dummy.DummyIntPropertyGenerator",Seq(num),Seq())
    val size = ExecutionPlan.StaticValue[Long](1000)
    val createPropertyTable = PropertyTable[Int]("int","property",generator,size)
    val tableSize = TableSize(createPropertyTable)
    TableSizeOperator(tableSize) should be (1000)
  }

  " An InstantiatePropertyGeneratorOperator" should "return an instance of a property generator " in {
    val num = ExecutionPlan.StaticValue[Int](1)
    val propertyGeneratorNode = ExecutionPlan.PropertyGenerator[Int]("org.dama.datasynth.common.generators.property.dummy.DummyIntPropertyGenerator",Seq(num),Seq())
    val generator = InstantiatePropertyGeneratorOperator("table.property", propertyGeneratorNode)
  }

  " An InstantiateGraphGeneratorOperator" should "return an instance of a property generator " in {
    val file1 = ExecutionPlan.StaticValue[String]("path/to/file")
    val file2 = ExecutionPlan.StaticValue[String]("path/to/file")
    val structureGeneratorNode = ExecutionPlan.StructureGenerator("org.dama.datasynth.common.generators.structure.BTERGenerator",Seq(file1, file2))
    val generator = InstantiateStructureGeneratorOperator(structureGeneratorNode)
  }

  "A FetchTableOperator" should "return a Dataset when fetching a table (either property or edge)" in {
    val num = ExecutionPlan.StaticValue[Int](1)
    val propertyGenerator = ExecutionPlan.PropertyGenerator[Int]("org.dama.datasynth.common.generators.property.dummy.DummyIntPropertyGenerator",Seq(num),Seq())
    val size = ExecutionPlan.StaticValue[Long](1000)
    val createPropertyTable = PropertyTable[Int]("int","property",propertyGenerator,size)
    FetchTableOperator(createPropertyTable)

    val file1 = ExecutionPlan.StaticValue[String]("src/main/resources/degrees/dblp")
    val file2 = ExecutionPlan.StaticValue[String]("src/main/resources/ccs/dblp")
    val structureGenerator = ExecutionPlan.StructureGenerator("org.dama.datasynth.common.generators.structure.BTERGenerator",Seq(file1, file2))
    val createEdgeTable = EdgeTable("edges",structureGenerator,size)
    FetchTableOperator(createEdgeTable)
  }

  "A FetchRndGeneratOperator generator" should "return a random generator which is unique for each table" in {

    val rnd1 = FetchRndGeneratorOperator("table1")
    val rnd1Prima = FetchRndGeneratorOperator("table1")
    val rnd2 = FetchRndGeneratorOperator("table2")
    val rnd2Prima = FetchRndGeneratorOperator("table2")
    rnd1 should be (rnd1Prima)
    rnd2 should be (rnd2Prima)
    rnd1 should not be (rnd2)
  }

  " An EvalValueOperator on a StaticValue[Boolean](true)" should " should return a Boolean true" in {
    val value = StaticValue[Boolean](true)
    EvalValueOperator(value).asInstanceOf[Boolean] should be (true)
  }

  " An EvalValueOperator on a StaticValue[Int](1)" should " should return an Int 1" in {
    val value = StaticValue[Int](1)
    EvalValueOperator(value).asInstanceOf[Int] should be (1)
  }

  " An EvalValueOperator on a StaticValue[Long](1)" should " should return a Long 1" in {
    val value = StaticValue[Long](1)
    EvalValueOperator(value).asInstanceOf[Long] should be (1)
  }

  " An EvalValueOperator on a StaticValue[Float](1.0)" should " should return a Float 1.0" in {
    val value = StaticValue[Float](1)
    EvalValueOperator(value).asInstanceOf[Float] should be (1.0)
  }

  " An EvalValueOperator on a StaticValue[Double](1.0)" should " should return a Double 1.0" in {
    val value = StaticValue[Double](1)
    EvalValueOperator(value).asInstanceOf[Double] should be (1.0)
  }

  " An EvalValueOperator on a StaticValue[String](\"text\")" should " should return a String \"text\"0" in {
    val value = StaticValue[String]("text")
    EvalValueOperator(value).asInstanceOf[String] should be ("text")
  }

}
