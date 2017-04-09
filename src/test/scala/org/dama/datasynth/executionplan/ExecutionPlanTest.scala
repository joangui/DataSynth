package org.dama.datasynth.executionplan

import org.dama.datasynth.executionplan.ExecutionPlan._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by aprat on 31/03/17.
  */
@RunWith(classOf[JUnitRunner])
class ExecutionPlanTest extends FlatSpec with Matchers{

  /** Generator tests **/
  "A PropertyGenerator toString" should "output like [PropertyGenerator,propertyGeneratorName]" in {
    val generator = new PropertyGenerator("path.to.class", List[Value[_]](), List[PropertyGenerator[_]]() )
    generator.toString should be ("[PropertyGenerator,path.to.class]")
  }

  "A GraphGenerator toString" should "output like [GraphGenerator,graphGeneratorName]" in {
    val generator = GraphGenerator("path.to.class", Seq())
    generator.toString should be ("[GraphGenerator,path.to.class]")
  }

  /** Parameter tests **/
  "A StaticValue[Long] toString" should "output like [StaticValue,Long,value]" in {
    val parameter = StaticValue[Long](10)
    parameter.toString should be ("[StaticValue[Long],10]")
  }

  "A StaticValue[String] toString" should "output like [StaticValue,String,value]" in {
    val parameter = StaticValue("test")
    parameter.toString should be ("[StaticValue[String],test]")
  }

  /** Tasks tests **/
  "A CreatePropertyTable toString" should "output like [CreatePropertyTable,typeName.PropertyName]" in {
    val generator = PropertyGenerator("path.to.generator",Seq(),Seq())
    val size = StaticValue[Long](10)
    val task = CreatePropertyTable("typeName","propertyName", generator,size)
    task.toString should be ("[CreatePropertyTable,typeName.propertyName]")
  }

  "A CreateEdgeTable toString" should "output like [CreateEdgeTable,tableName]" in {
    val generator = GraphGenerator("path.to.generator",Seq())
    val size = StaticValue[Long](10)
    val task = CreateEdgeTable("tableName",generator,size)
    task.toString should be ("[CreateEdgeTable,tableName]")
  }

  "A TableSize toString" should "output like [TableSize]" in {
    val generator = PropertyGenerator("path.to.generator",Seq(),Seq())
    val size = StaticValue[Long](10)
    val createTable = CreatePropertyTable("typeName","propertyName", generator,size)
    val task = TableSize(createTable)
    task.toString should be ("[TableSize]")
  }

  "A Match toString" should "output like [Match,tableNaem]" in {
    val propertyGenerator = PropertyGenerator("path.to.generator",Seq(),Seq())
    val size = StaticValue[Long](10)
    val createPropertyTable = CreatePropertyTable("typeName","propertyName",propertyGenerator,size)
    val graphGenerator = GraphGenerator("path.to.generator",Seq())
    val createEdgeTable = CreateEdgeTable("tableName",graphGenerator,size)
    val match_ = Match("tableName",createPropertyTable,createEdgeTable)
    match_.toString should be ("[Match,tableName]")
  }
}
